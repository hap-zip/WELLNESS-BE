package com.example.wellness.wellnesschat.service;

import com.example.wellness.wellnesschat.dto.TriggerType;
import com.example.wellness.wellnesschat.entity.PersistentSignal;
import com.example.wellness.wellnesschat.repository.PersistentSignalRepository;
import com.example.wellness.dailycheck.entity.DailyCheck;
import com.example.wellness.dailycheck.entity.DailyCheckPainArea;
import com.example.wellness.dailycheck.repository.DailyCheckRepository;
import com.example.wellness.dailyroutine.entity.DailyRoutineEntity;
import com.example.wellness.dailyroutine.repository.DailyRoutineRepository;
import com.example.wellness.feedback.entity.RoutineFeedbackEntity;
import com.example.wellness.feedback.repository.RoutineFeedbackRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * daily_check_pain_areas / daily_routines / routine_feedbacks 기록을 근거로
 * 지속신호(persistent_signals)를 판정하는 규칙 엔진. AI API 불필요, 순수 규칙 기반.
 *
 * 지속(PERSISTENT) / 악화(WORSENING) / 무개선(NO_IMPROVEMENT) 3가지 유형 모두 구현.
 */
@Service
public class PersistentSignalService {

    private final DailyCheckRepository dailyCheckRepository;
    private final PersistentSignalRepository persistentSignalRepository;
    private final DailyRoutineRepository dailyRoutineRepository;
    private final RoutineFeedbackRepository routineFeedbackRepository;
    private final int lookbackDays;
    private final int persistentThresholdDays;
    private final double worseningThreshold;
    private final int noImprovementMinCompletions;

    public PersistentSignalService(
            DailyCheckRepository dailyCheckRepository,
            PersistentSignalRepository persistentSignalRepository,
            DailyRoutineRepository dailyRoutineRepository,
            RoutineFeedbackRepository routineFeedbackRepository,
            @Value("${wellness.persistent-signal.lookback-days:7}") int lookbackDays,
            @Value("${wellness.persistent-signal.persistent-threshold-days:4}") int persistentThresholdDays,
            @Value("${wellness.persistent-signal.worsening-threshold:1.5}") double worseningThreshold,
            @Value("${wellness.persistent-signal.no-improvement-min-completions:3}") int noImprovementMinCompletions
    ) {
        this.dailyCheckRepository = dailyCheckRepository;
        this.persistentSignalRepository = persistentSignalRepository;
        this.dailyRoutineRepository = dailyRoutineRepository;
        this.routineFeedbackRepository = routineFeedbackRepository;
        this.lookbackDays = lookbackDays;
        this.persistentThresholdDays = persistentThresholdDays;
        this.worseningThreshold = worseningThreshold;
        this.noImprovementMinCompletions = noImprovementMinCompletions;
    }

    /**
     * 특정 유저에 대해 "지속" 신호를 검사하고, 조건 충족 시 새 신호를 저장한다.
     * 이미 해소 안 된 같은 부위 신호가 있으면 중복 생성하지 않는다.
     */
    @Transactional
    public List<PersistentSignal> checkPersistentSignals(Long userId) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(lookbackDays - 1L);

        List<DailyCheck> checks = dailyCheckRepository
                .findByUserIdAndCheckDateBetweenOrderByCheckDateAsc(userId, start, end);

        Map<String, Integer> zoneDayCount = new HashMap<>();
        for (DailyCheck check : checks) {
            for (DailyCheckPainArea area : check.getPainAreas()) {
                zoneDayCount.merge(area.getZoneId(), 1, Integer::sum);
            }
        }

        List<PersistentSignal> newSignals = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : zoneDayCount.entrySet()) {
            String zoneId = entry.getKey();
            int dayCount = entry.getValue();

            if (dayCount < persistentThresholdDays) {
                continue;
            }

            boolean alreadyActive = persistentSignalRepository
                    .findByUserIdAndPainAreaAndTriggerTypeAndResolvedAtIsNull(userId, zoneId, TriggerType.PERSISTENT)
                    .isPresent();
            if (alreadyActive) {
                continue;
            }

            PersistentSignal signal = new PersistentSignal();
            signal.setUserId(userId);
            signal.setPainArea(zoneId);
            signal.setTriggerType(TriggerType.PERSISTENT);
            signal.setStreakDays(dayCount);
            signal.setMessageSent(buildMessage(zoneId, dayCount));
            signal.setUserAcknowledged(false);

            newSignals.add(persistentSignalRepository.save(signal));
        }

        return newSignals;
    }

    /**
     * 특정 유저에 대해 "악화" 신호를 검사하고, 조건 충족 시 새 신호를 저장한다.
     * 최근 7일을 앞 절반/뒤 절반으로 나눠 평균 intensity 상승폭을 비교한다.
     */
    @Transactional
    public List<PersistentSignal> checkWorseningSignals(Long userId) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(lookbackDays - 1L);

        List<DailyCheck> checks = dailyCheckRepository
                .findByUserIdAndCheckDateBetweenOrderByCheckDateAsc(userId, start, end);

        int half = lookbackDays / 2;
        LocalDate midPoint = start.plusDays(half);

        Map<String, List<Integer>> earlyByZone = new HashMap<>();
        Map<String, List<Integer>> lateByZone = new HashMap<>();

        for (DailyCheck check : checks) {
            boolean isEarly = check.getCheckDate().isBefore(midPoint);
            for (DailyCheckPainArea area : check.getPainAreas()) {
                Map<String, List<Integer>> target = isEarly ? earlyByZone : lateByZone;
                target.computeIfAbsent(area.getZoneId(), k -> new ArrayList<>()).add(area.getIntensity());
            }
        }

        List<PersistentSignal> newSignals = new ArrayList<>();

        for (String zoneId : lateByZone.keySet()) {
            List<Integer> earlyValues = earlyByZone.getOrDefault(zoneId, List.of());
            List<Integer> lateValues = lateByZone.get(zoneId);

            if (earlyValues.isEmpty() || lateValues.isEmpty()) {
                continue;
            }

            double earlyAvg = earlyValues.stream().mapToInt(Integer::intValue).average().orElse(0);
            double lateAvg = lateValues.stream().mapToInt(Integer::intValue).average().orElse(0);
            double increase = lateAvg - earlyAvg;

            if (increase < worseningThreshold) {
                continue;
            }

            boolean alreadyActive = persistentSignalRepository
                    .findByUserIdAndPainAreaAndTriggerTypeAndResolvedAtIsNull(userId, zoneId, TriggerType.WORSENING)
                    .isPresent();
            if (alreadyActive) {
                continue;
            }

            PersistentSignal signal = new PersistentSignal();
            signal.setUserId(userId);
            signal.setPainArea(zoneId);
            signal.setTriggerType(TriggerType.WORSENING);
            signal.setStreakDays(checks.size());
            signal.setMessageSent(buildWorseningMessage(zoneId, earlyAvg, lateAvg));
            signal.setUserAcknowledged(false);

            newSignals.add(persistentSignalRepository.save(signal));
        }

        return newSignals;
    }

    /**
     * 특정 유저에 대해 "무개선" 신호를 검사하고, 조건 충족 시 새 신호를 저장한다.
     * 최근 lookbackDays일 내 같은 부위 루틴을 noImprovementMinCompletions회 이상 완료했는데
     * IMPROVED 피드백이 하나도 없으면 신호를 발생시킨다.
     */
    public List<PersistentSignal> checkNoImprovementSignals(Long userId) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(lookbackDays - 1L);

        List<DailyRoutineEntity> completedRoutines = dailyRoutineRepository
                .findAllByUserIdAndIsCompletedTrueAndTargetDateBetween(userId, start, end);

        Map<String, List<DailyRoutineEntity>> byArea = new HashMap<>();
        for (DailyRoutineEntity dr : completedRoutines) {
            String area = dr.getRoutine().getTargetArea();
            byArea.computeIfAbsent(area, k -> new ArrayList<>()).add(dr);
        }

        List<PersistentSignal> newSignals = new ArrayList<>();

        for (Map.Entry<String, List<DailyRoutineEntity>> entry : byArea.entrySet()) {
            String area = entry.getKey();
            List<DailyRoutineEntity> routines = entry.getValue();

            if (routines.size() < noImprovementMinCompletions) {
                continue;
            }

            boolean anyImproved = routines.stream().anyMatch(this::hasImprovedFeedback);
            if (anyImproved) {
                continue;
            }

            boolean alreadyActive = persistentSignalRepository
                    .findByUserIdAndPainAreaAndTriggerTypeAndResolvedAtIsNull(userId, area, TriggerType.NO_IMPROVEMENT)
                    .isPresent();
            if (alreadyActive) {
                continue;
            }

            PersistentSignal signal = new PersistentSignal();
            signal.setUserId(userId);
            signal.setPainArea(area);
            signal.setTriggerType(TriggerType.NO_IMPROVEMENT);
            signal.setStreakDays(routines.size());
            signal.setMessageSent(buildNoImprovementMessage(area, routines.size()));
            signal.setUserAcknowledged(false);

            newSignals.add(persistentSignalRepository.save(signal));
        }

        return newSignals;
    }

    private boolean hasImprovedFeedback(DailyRoutineEntity dr) {
        return isImproved(dr.getImmediateFeedbackId()) || isImproved(dr.getDelayedFeedbackId());
    }

    private boolean isImproved(Long feedbackId) {
        if (feedbackId == null) {
            return false;
        }
        return routineFeedbackRepository.findById(feedbackId)
                .map(f -> f.getEffectStatus() == RoutineFeedbackEntity.EffectStatus.IMPROVED)
                .orElse(false);
    }

    private String buildNoImprovementMessage(String area, int completionCount) {
        return String.format(
                "%s 관련 루틴을 %d회 수행했지만 아직 뚜렷한 개선이 없어요. (표본 %d회 기준)",
                area, completionCount, completionCount
        );
    }

    private String buildWorseningMessage(String zoneId, double earlyAvg, double lateAvg) {
        return String.format(
                "%s 부위의 불편함이 최근 강해지고 있어요. (초반 평균 %.1f점 → 최근 평균 %.1f점, 표본 %d일 기준)",
                zoneId, earlyAvg, lateAvg, lookbackDays
        );
    }

    private String buildMessage(String zoneId, int dayCount) {
        return String.format(
                "최근 %d일 중 %d일 동안 같은 부위(%s)에 불편함이 기록됐어요. (표본 %d일 기준)",
                lookbackDays, dayCount, zoneId, lookbackDays
        );
    }
}
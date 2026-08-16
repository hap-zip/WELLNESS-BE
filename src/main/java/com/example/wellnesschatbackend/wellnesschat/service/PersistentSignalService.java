package com.example.wellnesschatbackend.wellnesschat.service;

import com.example.wellnesschatbackend.wellnesschat.dto.TriggerType;
import com.example.wellnesschatbackend.wellnesschat.entity.PersistentSignal;
import com.example.wellnesschatbackend.wellnesschat.repository.PersistentSignalRepository;
import com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.entity.DailyCheck;
import com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.entity.DailyCheckPainArea;
import com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.repository.DailyCheckRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * daily_check_pain_areas 기록을 근거로 지속신호(persistent_signals)를 판정하는 규칙 엔진.
 * AI API 불필요, 순수 규칙 기반.
 *
 * 지금은 "지속(PERSISTENT)" 유형만 구현. 악화/무개선은 다음 단계.
 */
@Service
public class PersistentSignalService {

    private final DailyCheckRepository dailyCheckRepository;
    private final PersistentSignalRepository persistentSignalRepository;
    private final int lookbackDays;
    private final int persistentThresholdDays;

    public PersistentSignalService(
            DailyCheckRepository dailyCheckRepository,
            PersistentSignalRepository persistentSignalRepository,
            @Value("${wellness.persistent-signal.lookback-days:7}") int lookbackDays,
            @Value("${wellness.persistent-signal.persistent-threshold-days:4}") int persistentThresholdDays
    ) {
        this.dailyCheckRepository = dailyCheckRepository;
        this.persistentSignalRepository = persistentSignalRepository;
        this.lookbackDays = lookbackDays;
        this.persistentThresholdDays = persistentThresholdDays;
    }

    /**
     * 특정 유저에 대해 "지속" 신호를 검사하고, 조건 충족 시 새 신호를 저장한다.
     * 이미 해소 안 된 같은 부위 신호가 있으면 중복 생성하지 않는다.
     */
    public List<PersistentSignal> checkPersistentSignals(Long userId) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(lookbackDays - 1L);

        List<DailyCheck> checks = dailyCheckRepository
                .findByUserIdAndCheckDateBetweenOrderByCheckDateAsc(userId, start, end);

        // zoneId별로 등장한 날짜 수 카운트
        Map<String, Integer> zoneDayCount = new HashMap<>();
        for (DailyCheck check : checks) {
            for (DailyCheckPainArea area : check.getPainAreas()) {
                zoneDayCount.merge(area.getZoneId(), 1, Integer::sum);
            }
        }

        List<PersistentSignal> newSignals = new java.util.ArrayList<>();

        for (Map.Entry<String, Integer> entry : zoneDayCount.entrySet()) {
            String zoneId = entry.getKey();
            int dayCount = entry.getValue();

            if (dayCount < persistentThresholdDays) {
                continue;
            }

            // 이미 활성화(미해소)된 같은 부위·같은 유형 신호가 있으면 건너뜀 (중복 방지)
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

    private String buildMessage(String zoneId, int dayCount) {
        return String.format(
                "최근 %d일 중 %d일 동안 같은 부위(%s)에 불편함이 기록됐어요. (표본 %d일 기준)",
                lookbackDays, dayCount, zoneId, lookbackDays
        );
    }
}
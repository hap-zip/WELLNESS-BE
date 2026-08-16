package com.example.wellnesschatbackend.expertcard.service;

import com.example.wellnesschatbackend.expertcard.dto.request.ExpertCardRequest;
import com.example.wellnesschatbackend.expertcard.dto.response.ExpertCardResponse;
import com.example.wellnesschatbackend.dailycheck.entity.DailyCheck;
import com.example.wellnesschatbackend.dailycheck.entity.DailyCheckPainArea;
import com.example.wellnesschatbackend.dailycheck.enums.SleepPosture;
import com.example.wellnesschatbackend.expertcard.entity.ExpertCard;
import com.example.wellnesschatbackend.expertcard.enums.ReportPeriod;
import com.example.wellnesschatbackend.exception.ErrorCode;
import com.example.wellnesschatbackend.exception.NotFoundException;
import com.example.wellnesschatbackend.dailycheck.repository.DailyCheckRepository;
import com.example.wellnesschatbackend.expertcard.repository.ExpertCardRepository;
import com.example.wellnesschatbackend.health.entity.HealthData;
import com.example.wellnesschatbackend.health.repository.HealthDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Service
public class ExpertCardService {

    private static final String DISCLAIMER = "이 요약은 직접 기록한 생활 데이터에 기반하며 의료 진단서가 아니에요.";

    private final ExpertCardRepository expertCardRepository;
    private final DailyCheckRepository dailyCheckRepository;
    private final HealthDataRepository healthDataRepository;

    public ExpertCardService(
            ExpertCardRepository expertCardRepository,
            DailyCheckRepository dailyCheckRepository,
            HealthDataRepository healthDataRepository
    ) {
        this.expertCardRepository = expertCardRepository;
        this.dailyCheckRepository = dailyCheckRepository;
        this.healthDataRepository = healthDataRepository;
    }

    @Transactional
    public ExpertCardResponse create(Long userId, ExpertCardRequest request) {
        ReportPeriod period = ReportPeriod.fromWireValue(request.period());
        DateRange range = resolveRange(period, request.startDate(), request.endDate());

        List<DailyCheck> checks = dailyCheckRepository.findByUserIdAndCheckDateBetweenOrderByCheckDateAsc(userId,
                range.start(), range.end());
        DateRange previousRange = previousRange(range);
        List<DailyCheck> previousChecks = dailyCheckRepository
                .findByUserIdAndCheckDateBetweenOrderByCheckDateAsc(userId, previousRange.start(), previousRange.end());

        List<ExpertCard.Highlight> highlights = new ArrayList<>();
        List<String> discomfortAreas = List.of();
        List<String> sleepPostures = List.of();

        if (request.includeSleep()) {
            highlights.add(sleepHighlight(checks, previousChecks));
            sleepPostures = distinctPostures(checks);
        }
        if (request.includeActivity()) {
            highlights.add(activityHighlight(checks, previousChecks));
        }
        if (request.includeDiscomfort()) {
            highlights.add(discomfortHighlight(checks));
            discomfortAreas = distinctZones(checks);
        }

        String headline = buildHeadline(checks, request.includeDiscomfort());

        ExpertCard card = new ExpertCard();
        card.setUserId(userId);
        card.setPeriod(period);
        card.setStartDate(range.start());
        card.setEndDate(range.end());
        card.setIncludeSleep(request.includeSleep());
        card.setIncludeActivity(request.includeActivity());
        card.setIncludeDiscomfort(request.includeDiscomfort());
        card.setIncludeRoutines(request.includeRoutines());
        card.setHidePersonalInfo(request.hidePersonalInfo());
        card.setHeadline(headline);
        card.setHighlights(highlights);
        card.setDiscomfortAreas(discomfortAreas);
        card.setSleepPostures(sleepPostures);
        // 루틴 실행,피드백은 이 서비스가 소유한 데이터가 아니라 0/빈 값으로 둔다. 루틴 백엔드 연동 후 채워야 한다.
        card.setRoutineCount(0);
        card.setFeedbackSummary(null);
        card.setDiscoveredPatterns(List.of());
        card.setNote(DISCLAIMER);

        return toResponse(expertCardRepository.save(card));
    }

    @Transactional(readOnly = true)
    public List<ExpertCardResponse> list(Long userId) {
        return expertCardRepository.findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExpertCardResponse get(Long userId, Long cardId) {
        ExpertCard card = expertCardRepository.findByIdAndUserIdAndDeletedAtIsNull(cardId, userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.EXPERT_CARD_NOT_FOUND, "요약 카드를 찾을 수 없어요."));
        return toResponse(card);
    }

    @Transactional
    public void delete(Long userId, Long cardId) {
        ExpertCard card = expertCardRepository.findByIdAndUserIdAndDeletedAtIsNull(cardId, userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.EXPERT_CARD_NOT_FOUND, "요약 카드를 찾을 수 없어요."));
        card.setDeletedAt(LocalDateTime.now());
    }

    private DateRange resolveRange(ReportPeriod period, String startDate, String endDate) {
        LocalDate today = LocalDate.now();
        return switch (period) {
            case THREE_DAYS -> new DateRange(today.minusDays(2), today);
            case SEVEN_DAYS -> new DateRange(today.minusDays(6), today);
            case FOURTEEN_DAYS -> new DateRange(today.minusDays(13), today);
            case CUSTOM -> {
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("custom 기간은 startDate와 endDate가 모두 필요해요.");
                }
                LocalDate start = LocalDate.parse(startDate);
                LocalDate end = LocalDate.parse(endDate);
                if (end.isBefore(start)) {
                    throw new IllegalArgumentException("endDate는 startDate보다 앞설 수 없어요.");
                }
                yield new DateRange(start, end);
            }
        };
    }

    private DateRange previousRange(DateRange range) {
        long days = ChronoUnit.DAYS.between(range.start(), range.end()) + 1;
        LocalDate previousEnd = range.start().minusDays(1);
        LocalDate previousStart = previousEnd.minusDays(days - 1);
        return new DateRange(previousStart, previousEnd);
    }

    private ExpertCard.Highlight sleepHighlight(List<DailyCheck> checks, List<DailyCheck> previousChecks) {
        OptionalDouble current = averageSleepMinutes(checks);
        OptionalDouble previous = averageSleepMinutes(previousChecks);
        String value = current.isPresent() ? formatDuration(current.getAsDouble()) : "기록 없음";
        String change = describeChange(current, previous, "분");
        return new ExpertCard.Highlight("평균 수면", value, change);
    }

    private ExpertCard.Highlight activityHighlight(List<DailyCheck> checks, List<DailyCheck> previousChecks) {
        OptionalDouble current = averageSteps(checks);
        OptionalDouble previous = averageSteps(previousChecks);
        String value = current.isPresent() ? String.format("%,d보", Math.round(current.getAsDouble())) : "기록 없음";
        String change = describeChange(current, previous, "보");
        return new ExpertCard.Highlight("평균 걸음", value, change);
    }

    private ExpertCard.Highlight discomfortHighlight(List<DailyCheck> checks) {
        Map<String, Long> counts = checks.stream()
                .flatMap(check -> check.getPainAreas().stream())
                .collect(Collectors.groupingBy(DailyCheckPainArea::getZoneId, Collectors.counting()));
        if (counts.isEmpty()) {
            return new ExpertCard.Highlight("불편 기록", "기록 없음", "-");
        }
        Map.Entry<String, Long> top = counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow();
        return new ExpertCard.Highlight("불편 기록", top.getKey() + " " + top.getValue() + "일", "가장 자주 기록");
    }

    private List<String> distinctZones(List<DailyCheck> checks) {
        return checks.stream()
                .flatMap(check -> check.getPainAreas().stream())
                .map(DailyCheckPainArea::getZoneId)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> distinctPostures(List<DailyCheck> checks) {
        return checks.stream()
                .map(DailyCheck::getSleepPosture)
                .filter(Objects::nonNull)
                .map(SleepPosture::getWireValue)
                .distinct()
                .collect(Collectors.toList());
    }

    /** override(daily_checks.auto_*)가 있으면 그걸, 없으면 health_data 원본을 쓴다. */
    private OptionalDouble averageSleepMinutes(List<DailyCheck> checks) {
        return checks.stream().map(this::effectiveSleepMinutes).filter(Objects::nonNull)
                .mapToInt(Integer::intValue).average();
    }

    private OptionalDouble averageSteps(List<DailyCheck> checks) {
        return checks.stream().map(this::effectiveSteps).filter(Objects::nonNull).mapToInt(Integer::intValue)
                .average();
    }

    private Integer effectiveSleepMinutes(DailyCheck check) {
        if (check.getAutoSleepDurationMinutes() != null) return check.getAutoSleepDurationMinutes();
        return healthDataRepository.findByUserIdAndRecordDate(check.getUserId(), check.getCheckDate())
                .map(HealthData::getSleepDurationMinutes)
                .orElse(null);
    }

    private Integer effectiveSteps(DailyCheck check) {
        if (check.getAutoSteps() != null) return check.getAutoSteps();
        return healthDataRepository.findByUserIdAndRecordDate(check.getUserId(), check.getCheckDate())
                .map(HealthData::getSteps)
                .orElse(null);
    }

    private String describeChange(OptionalDouble current, OptionalDouble previous, String unit) {
        if (current.isEmpty() || previous.isEmpty())
            return "이전 기록 없음";
        double diff = current.getAsDouble() - previous.getAsDouble();
        if (Math.abs(diff) < 0.5)
            return "이전과 비슷해요";
        String direction = diff > 0 ? "증가" : "감소";
        return String.format("이전보다 %d%s %s", Math.round(Math.abs(diff)), unit, direction);
    }

    private String formatDuration(double minutes) {
        long total = Math.round(minutes);
        return String.format("%d시간 %d분", total / 60, total % 60);
    }

    private String buildHeadline(List<DailyCheck> checks, boolean includeDiscomfort) {
        if (checks.isEmpty())
            return "이 기간에는 기록이 없어요.";
        if (includeDiscomfort) {
            List<String> zones = distinctZones(checks);
            if (!zones.isEmpty()) {
                return checks.size() + "일 중 " + zones.size() + "개 부위에서 불편이 기록됐어요.";
            }
        }
        return checks.size() + "일의 기록을 정리했어요.";
    }

    private ExpertCardResponse toResponse(ExpertCard card) {
        List<ExpertCardResponse.HighlightResponse> highlights = card.getHighlights().stream()
                .map(highlight -> new ExpertCardResponse.HighlightResponse(highlight.getLabel(), highlight.getValue(),
                        highlight.getChange()))
                .collect(Collectors.toList());

        return new ExpertCardResponse(
                card.getId(),
                card.getPeriod().getWireValue(),
                card.getStartDate().toString(),
                card.getEndDate().toString(),
                card.getHeadline(),
                highlights,
                card.getDiscomfortAreas(),
                card.getSleepPostures(),
                card.getRoutineCount(),
                card.getFeedbackSummary(),
                card.getDiscoveredPatterns(),
                card.getNote(),
                card.getCreatedAt().toString());
    }

    private record DateRange(LocalDate start, LocalDate end) {
    }
}

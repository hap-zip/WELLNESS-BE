package com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.service;

import com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.dto.request.DailyCheckRequest;
import com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.dto.response.DailyCheckResponse;
import com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.dto.response.RecordsMonthResponse;
import com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.entity.DailyCheck;
import com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.entity.DailyCheckPainArea;
import com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.enums.AutoSource;
import com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.enums.BodyView;
import com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.enums.Condition;
import com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.enums.SleepPosture;
import com.example.wellnesschatbackend.wellnessdailyexpert.exception.ErrorCode;
import com.example.wellnesschatbackend.wellnessdailyexpert.exception.NotFoundException;
import com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.repository.DailyCheckRepository;
import com.example.wellnesschatbackend.wellnessdailyexpert.health.entity.HealthData;
import com.example.wellnesschatbackend.wellnessdailyexpert.health.repository.HealthDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DailyCheckService {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final DailyCheckRepository dailyCheckRepository;
    private final HealthDataRepository healthDataRepository;

    public DailyCheckService(DailyCheckRepository dailyCheckRepository, HealthDataRepository healthDataRepository) {
        this.dailyCheckRepository = dailyCheckRepository;
        this.healthDataRepository = healthDataRepository;
    }

    @Transactional
    public DailyCheckResponse save(Long userId, LocalDate checkDate, DailyCheckRequest request) {
        DailyCheck dailyCheck = dailyCheckRepository.findByUserIdAndCheckDate(userId, checkDate)
                .orElseGet(DailyCheck::new);
        dailyCheck.setUserId(userId);
        dailyCheck.setCheckDate(checkDate);
        applyRequest(dailyCheck, request);
        DailyCheck saved = dailyCheckRepository.save(dailyCheck);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public DailyCheckResponse getDetail(Long userId, LocalDate checkDate) {
        DailyCheck dailyCheck = dailyCheckRepository.findByUserIdAndCheckDate(userId, checkDate)
                .orElseThrow(() -> new NotFoundException(ErrorCode.DAILY_CHECK_NOT_FOUND, checkDate + " 기록이 없어요."));
        return toResponse(dailyCheck);
    }

    @Transactional(readOnly = true)
    public RecordsMonthResponse getMonth(Long userId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        List<DailyCheck> checks = dailyCheckRepository.findByUserIdAndCheckDateBetweenOrderByCheckDateAsc(userId, start, end);

        List<RecordsMonthResponse.DayRecordResponse> records = checks.stream()
                .map(check -> new RecordsMonthResponse.DayRecordResponse(
                        check.getId(),
                        check.getCheckDate().toString(),
                        check.getCondition() == null ? null : check.getCondition().getWireValue(),
                        check.maxPainIntensity(),
                        check.getPainAreas().stream().map(DailyCheckPainArea::getZoneId).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        int discomfortDays = (int) checks.stream().filter(check -> !check.getPainAreas().isEmpty()).count();
        OptionalDouble average = checks.stream()
                .map(this::effectiveSleepMinutes)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average();
        Double averageSleepMinutes = average.isPresent() ? average.getAsDouble() : null;

        return new RecordsMonthResponse(year, month, records, new RecordsMonthResponse.MonthStats(checks.size(), averageSleepMinutes, discomfortDays));
    }

    @Transactional
    public void delete(Long userId, LocalDate checkDate) {
        DailyCheck dailyCheck = dailyCheckRepository.findByUserIdAndCheckDate(userId, checkDate)
                .orElseThrow(() -> new NotFoundException(ErrorCode.DAILY_CHECK_NOT_FOUND, checkDate + " 기록이 없어요."));
        dailyCheckRepository.delete(dailyCheck);
    }

    private void applyRequest(DailyCheck dailyCheck, DailyCheckRequest request) {
        dailyCheck.setCondition(request.condition() == null ? null : Condition.fromWireValue(request.condition()));
        dailyCheck.setConditionTags(request.conditionTags());

        if (request.discomfort() != null) {
            dailyCheck.setDiscomfortFeelings(request.discomfort().feelings());
            List<DailyCheckPainArea> areas = request.discomfort().areas() == null
                    ? List.of()
                    : request.discomfort().areas().stream()
                        .map(areaRequest -> {
                            DailyCheckPainArea area = new DailyCheckPainArea();
                            area.setZoneId(areaRequest.zoneId());
                            area.setView(BodyView.fromWireValue(areaRequest.view()));
                            area.setIntensity(areaRequest.intensity());
                            return area;
                        })
                        .collect(Collectors.toList());
            requireDistinctZoneIds(areas);
            dailyCheck.replacePainAreas(areas);
        } else {
            dailyCheck.replacePainAreas(List.of());
        }

        if (request.sleep() != null) {
            dailyCheck.setSleepSatisfaction(request.sleep().satisfaction());
            dailyCheck.setSleepPosture(request.sleep().posture() == null ? null : SleepPosture.fromWireValue(request.sleep().posture()));
            dailyCheck.setSleepPillow(request.sleep().pillow());
        }

        if (request.activitySkin() != null) {
            dailyCheck.setActivityLevel(request.activitySkin().activity());
            dailyCheck.setSkinStates(request.activitySkin().skinStates());
            dailyCheck.setMemo(request.activitySkin().memo());
        }

        if (request.autoRecords() != null) {
            applyAutoRecord(dailyCheck, request);
        }

        dailyCheck.setSkippedSteps(request.skippedSteps());
    }

    /**
     * health_data가 원본, daily_checks.auto_*는 유저가 값을 고쳤을 때만 채우는 덮어쓰기 필드다.
     * 들어온 값이 health_data랑 같으면(안 고침) 비워두고, 다르면(고침) 그대로 저장한다.
     */
    private void applyAutoRecord(DailyCheck dailyCheck, DailyCheckRequest request) {
        Integer sleepMinutes = request.autoRecords().sleepDurationMinutes();
        LocalTime bedtime = parseTime(request.autoRecords().bedtime());
        Integer steps = request.autoRecords().steps();
        AutoSource source = request.autoRecords().source() == null ? null : AutoSource.fromWireValue(request.autoRecords().source());

        Optional<HealthData> healthData = healthDataRepository.findByUserIdAndRecordDate(dailyCheck.getUserId(), dailyCheck.getCheckDate());
        boolean matchesHealthData = healthData.isPresent()
                && Objects.equals(healthData.get().getSleepDurationMinutes(), sleepMinutes)
                && Objects.equals(healthData.get().getBedtime(), bedtime)
                && Objects.equals(healthData.get().getSteps(), steps)
                && healthData.get().getSource() == source;

        if (matchesHealthData) {
            dailyCheck.setAutoSleepDurationMinutes(null);
            dailyCheck.setAutoBedtime(null);
            dailyCheck.setAutoSteps(null);
            dailyCheck.setAutoSource(null);
        } else {
            dailyCheck.setAutoSleepDurationMinutes(sleepMinutes);
            dailyCheck.setAutoBedtime(bedtime);
            dailyCheck.setAutoSteps(steps);
            dailyCheck.setAutoSource(source);
        }
    }

    /** 같은 zoneId가 중복으로 들어오면 (daily_check_id, zone_id) 유니크 제약 위반으로 500이 나므로 저장 전에 막는다. */
    private void requireDistinctZoneIds(List<DailyCheckPainArea> areas) {
        Set<String> seen = new HashSet<>();
        for (DailyCheckPainArea area : areas) {
            if (!seen.add(area.getZoneId())) {
                throw new IllegalArgumentException("같은 부위(zoneId)를 중복으로 보낼 수 없어요: " + area.getZoneId());
            }
        }
    }

    private LocalTime parseTime(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return LocalTime.parse(value, TIME_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("bedtime은 HH:mm 형식이어야 해요: " + value);
        }
    }

    private DailyCheckResponse toResponse(DailyCheck dailyCheck) {
        List<DailyCheckResponse.PainAreaResponse> areas = dailyCheck.getPainAreas().stream()
                .map(area -> new DailyCheckResponse.PainAreaResponse(area.getZoneId(), area.getView().getWireValue(), area.getIntensity()))
                .collect(Collectors.toList());

        return new DailyCheckResponse(
                dailyCheck.getId(),
                dailyCheck.getCheckDate().toString(),
                dailyCheck.getCondition() == null ? null : dailyCheck.getCondition().getWireValue(),
                dailyCheck.getConditionTags(),
                dailyCheck.getDiscomfortFeelings(),
                dailyCheck.maxPainIntensity(),
                areas,
                new DailyCheckResponse.SleepResponse(
                        dailyCheck.getSleepSatisfaction(),
                        dailyCheck.getSleepPosture() == null ? null : dailyCheck.getSleepPosture().getWireValue(),
                        dailyCheck.getSleepPillow()
                ),
                new DailyCheckResponse.ActivitySkinResponse(dailyCheck.getActivityLevel(), dailyCheck.getSkinStates()),
                effectiveAutoRecord(dailyCheck),
                dailyCheck.getMemo(),
                dailyCheck.getSkippedSteps()
        );
    }

    private boolean hasAutoOverride(DailyCheck dailyCheck) {
        return dailyCheck.getAutoSleepDurationMinutes() != null
                || dailyCheck.getAutoBedtime() != null
                || dailyCheck.getAutoSteps() != null
                || dailyCheck.getAutoSource() != null;
    }

    /** override가 있으면 그걸, 없으면 health_data 원본을 응답으로 돌려준다. */
    private DailyCheckResponse.AutoRecordResponse effectiveAutoRecord(DailyCheck dailyCheck) {
        if (hasAutoOverride(dailyCheck)) {
            return new DailyCheckResponse.AutoRecordResponse(
                    dailyCheck.getAutoSleepDurationMinutes(),
                    dailyCheck.getAutoBedtime() == null ? null : dailyCheck.getAutoBedtime().format(TIME_FORMAT),
                    dailyCheck.getAutoSteps(),
                    dailyCheck.getAutoSource() == null ? null : dailyCheck.getAutoSource().getWireValue()
            );
        }
        return healthDataRepository.findByUserIdAndRecordDate(dailyCheck.getUserId(), dailyCheck.getCheckDate())
                .map(healthData -> new DailyCheckResponse.AutoRecordResponse(
                        healthData.getSleepDurationMinutes(),
                        healthData.getBedtime() == null ? null : healthData.getBedtime().format(TIME_FORMAT),
                        healthData.getSteps(),
                        healthData.getSource() == null ? null : healthData.getSource().getWireValue()
                ))
                .orElseGet(() -> new DailyCheckResponse.AutoRecordResponse(null, null, null, null));
    }

    private Integer effectiveSleepMinutes(DailyCheck dailyCheck) {
        if (dailyCheck.getAutoSleepDurationMinutes() != null) return dailyCheck.getAutoSleepDurationMinutes();
        return healthDataRepository.findByUserIdAndRecordDate(dailyCheck.getUserId(), dailyCheck.getCheckDate())
                .map(HealthData::getSleepDurationMinutes)
                .orElse(null);
    }
}

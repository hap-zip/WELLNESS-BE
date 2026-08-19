package com.example.wellness.dailycheck.service;

import com.example.wellness.dailycheck.dto.request.DailyCheckRequest;
import com.example.wellness.dailycheck.dto.response.DailyCheckResponse;
import com.example.wellness.dailycheck.dto.response.RecordsMonthResponse;
import com.example.wellness.dailycheck.entity.DailyCheck;
import com.example.wellness.dailycheck.entity.DailyCheckPainArea;
import com.example.wellness.dailycheck.enums.AutoSource;
import com.example.wellness.dailycheck.enums.BodyView;
import com.example.wellness.dailycheck.enums.Condition;
import com.example.wellness.dailycheck.enums.SleepPosture;
import com.example.wellness.dailyroutine.entity.RoutineEntity;
import com.example.wellness.exception.ErrorCode;
import com.example.wellness.exception.NotFoundException;
import com.example.wellness.dailycheck.repository.DailyCheckRepository;
import com.example.wellness.health.entity.HealthData;
import com.example.wellness.health.repository.HealthDataRepository;
import com.example.wellness.connectionview.service.PatternAnalysisService;
import com.example.wellness.dailyroutine.entity.DailyRoutineEntity;
import com.example.wellness.dailyroutine.repository.DailyRoutineRepository;
import com.example.wellness.dailyroutine.repository.RoutineRepository;

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
    private final PatternAnalysisService patternAnalysisService;
    private final RoutineRepository routineRepository;
    private final DailyRoutineRepository dailyRoutineRepository;

    public DailyCheckService(DailyCheckRepository dailyCheckRepository, HealthDataRepository healthDataRepository,
                             PatternAnalysisService patternAnalysisService, RoutineRepository routineRepository, DailyRoutineRepository dailyRoutineRepository) {
        this.dailyCheckRepository = dailyCheckRepository;
        this.healthDataRepository = healthDataRepository;
        this.patternAnalysisService = patternAnalysisService;
        this.routineRepository = routineRepository;
        this.dailyRoutineRepository = dailyRoutineRepository;
    }

    @Transactional
    public DailyCheckResponse save(Long userId, LocalDate checkDate, DailyCheckRequest request) {
        DailyCheck dailyCheck = dailyCheckRepository.findByUserIdAndCheckDate(userId, checkDate)
                .orElseGet(DailyCheck::new);
        dailyCheck.setUserId(userId);
        dailyCheck.setCheckDate(checkDate);
        applyRequest(dailyCheck, request);
        DailyCheck saved = dailyCheckRepository.save(dailyCheck);
        patternAnalysisService.analyzeUserPatterns(userId);
        createRoutineIfPainAreaExists(userId, checkDate, request);
        return toResponse(saved);
    }

    private void createRoutineIfPainAreaExists(Long userId, LocalDate checkDate, DailyCheckRequest request) {
        if (request.discomfort() != null && request.discomfort().areas() != null && !request.discomfort().areas().isEmpty()) {
            var firstArea = request.discomfort().areas().get(0);
            String view = firstArea.view().toLowerCase();
            String zoneId = firstArea.zoneId().toLowerCase();
            String combinedTarget = view + "-" + zoneId;
            boolean hasRoutine = dailyRoutineRepository.findByUserIdAndTargetDate(userId, checkDate).isPresent();
            if (!hasRoutine) {
                RoutineEntity matchedRoutine = routineRepository.findByTargetArea(combinedTarget)
                        .orElseGet(() -> routineRepository.findByTargetArea(zoneId).orElse(null));
                if (matchedRoutine != null) {
                    DailyRoutineEntity dailyRoutine = DailyRoutineEntity.builder()
                            .userId(userId)
                            .routine(matchedRoutine)
                            .targetDate(checkDate)
                            .build();
                    dailyRoutineRepository.save(dailyRoutine);
                }
            }
        }
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
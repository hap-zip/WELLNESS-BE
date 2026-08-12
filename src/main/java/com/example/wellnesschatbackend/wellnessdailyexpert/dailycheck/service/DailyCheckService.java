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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DailyCheckService {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final DailyCheckRepository dailyCheckRepository;

    public DailyCheckService(DailyCheckRepository dailyCheckRepository) {
        this.dailyCheckRepository = dailyCheckRepository;
    }

    @Transactional
    public DailyCheckResponse save(UUID userId, LocalDate checkDate, DailyCheckRequest request) {
        DailyCheck dailyCheck = dailyCheckRepository.findByUserIdAndCheckDate(userId, checkDate)
                .orElseGet(DailyCheck::new);
        dailyCheck.setUserId(userId);
        dailyCheck.setCheckDate(checkDate);
        applyRequest(dailyCheck, request);
        DailyCheck saved = dailyCheckRepository.save(dailyCheck);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public DailyCheckResponse getDetail(UUID userId, LocalDate checkDate) {
        DailyCheck dailyCheck = dailyCheckRepository.findByUserIdAndCheckDate(userId, checkDate)
                .orElseThrow(() -> new NotFoundException(ErrorCode.DAILY_CHECK_NOT_FOUND, checkDate + " 기록이 없어요."));
        return toResponse(dailyCheck);
    }

    @Transactional(readOnly = true)
    public RecordsMonthResponse getMonth(UUID userId, int year, int month) {
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
                .map(DailyCheck::getAutoSleepDurationMinutes)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average();
        Double averageSleepMinutes = average.isPresent() ? average.getAsDouble() : null;

        return new RecordsMonthResponse(year, month, records, new RecordsMonthResponse.MonthStats(checks.size(), averageSleepMinutes, discomfortDays));
    }

    @Transactional
    public void delete(UUID userId, LocalDate checkDate) {
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
            dailyCheck.setAutoSleepDurationMinutes(request.autoRecords().sleepDurationMinutes());
            dailyCheck.setAutoBedtime(parseTime(request.autoRecords().bedtime()));
            dailyCheck.setAutoSteps(request.autoRecords().steps());
            dailyCheck.setAutoSource(request.autoRecords().source() == null ? null : AutoSource.fromWireValue(request.autoRecords().source()));
        }

        dailyCheck.setSkippedSteps(request.skippedSteps());
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
                new DailyCheckResponse.AutoRecordResponse(
                        dailyCheck.getAutoSleepDurationMinutes(),
                        dailyCheck.getAutoBedtime() == null ? null : dailyCheck.getAutoBedtime().format(TIME_FORMAT),
                        dailyCheck.getAutoSteps(),
                        dailyCheck.getAutoSource() == null ? null : dailyCheck.getAutoSource().getWireValue()
                ),
                dailyCheck.getMemo(),
                dailyCheck.getSkippedSteps()
        );
    }
}

package com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.dto.response;

import java.util.List;
import java.util.UUID;

public record DailyCheckResponse(
        UUID id,
        String date,
        String condition,
        List<String> conditionTags,
        List<String> feelings,
        Integer intensity,
        List<PainAreaResponse> painAreas,
        SleepResponse sleep,
        ActivitySkinResponse activitySkin,
        AutoRecordResponse autoRecords,
        String memo,
        List<String> skippedSteps
) {

    public record PainAreaResponse(String zoneId, String view, Integer intensity) {}

    public record SleepResponse(Integer satisfaction, String posture, String pillow) {}

    public record ActivitySkinResponse(String activity, List<String> skinStates) {}

    public record AutoRecordResponse(Integer sleepDurationMinutes, String bedtime, Integer steps, String source) {}
}

package com.example.wellness.dailycheck.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DailyCheckRequest(
        @Valid AutoRecordRequest autoRecords,
        String condition,
        List<String> conditionTags,
        @Valid DiscomfortRequest discomfort,
        @Valid SleepRequest sleep,
        @Valid ActivitySkinRequest activitySkin,
        List<String> skippedSteps
) {

    public record AutoRecordRequest(
            Integer sleepDurationMinutes,
            String bedtime,
            Integer steps,
            String source
    ) {}

    public record DiscomfortRequest(
            List<String> feelings,
            @Valid List<PainAreaRequest> areas
    ) {}

    public record PainAreaRequest(
            @NotBlank String zoneId,
            @NotBlank String view,
            @NotNull @Min(1) @Max(5) Integer intensity
    ) {}

    public record SleepRequest(
            @Min(1) @Max(5) Integer satisfaction,
            String posture,
            String pillow
    ) {}

    public record ActivitySkinRequest(
            String activity,
            List<String> skinStates,
            String memo
    ) {}
}

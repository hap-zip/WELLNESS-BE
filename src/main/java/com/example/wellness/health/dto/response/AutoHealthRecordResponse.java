package com.example.wellness.health.dto.response;

public record AutoHealthRecordResponse(
        Integer sleepDurationMinutes,
        String bedtime,
        Integer steps,
        String source
) {}

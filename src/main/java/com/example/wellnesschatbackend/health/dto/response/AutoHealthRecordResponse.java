package com.example.wellnesschatbackend.health.dto.response;

public record AutoHealthRecordResponse(
        Integer sleepDurationMinutes,
        String bedtime,
        Integer steps,
        String source
) {}

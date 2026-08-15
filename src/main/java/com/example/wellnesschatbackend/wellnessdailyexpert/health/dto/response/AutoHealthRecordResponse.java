package com.example.wellnesschatbackend.wellnessdailyexpert.health.dto.response;

public record AutoHealthRecordResponse(
        Integer sleepDurationMinutes,
        String bedtime,
        Integer steps,
        String source
) {}

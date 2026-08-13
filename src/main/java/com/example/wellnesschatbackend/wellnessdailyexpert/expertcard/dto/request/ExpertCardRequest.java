package com.example.wellnesschatbackend.wellnessdailyexpert.expertcard.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ExpertCardRequest(
        @NotBlank String period,
        String startDate,
        String endDate,
        boolean includeSleep,
        boolean includeActivity,
        boolean includeDiscomfort,
        boolean includeRoutines,
        boolean hidePersonalInfo
) {}

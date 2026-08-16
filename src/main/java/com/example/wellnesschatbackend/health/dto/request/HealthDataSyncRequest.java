package com.example.wellnesschatbackend.health.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HealthDataSyncRequest(
        @NotNull String date,
        Integer sleepDurationMinutes,
        String bedtime,
        Integer steps,
        @NotBlank String source
) {}

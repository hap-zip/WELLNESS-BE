package com.example.wellnesschatbackend.wellnessdailyexpert.health.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HealthConnectionRequest(
        @NotBlank String provider,
        @NotNull Boolean connected,
        @NotNull Permissions permissions
) {
    public record Permissions(boolean sleep, boolean steps, boolean heartRate) {}
}

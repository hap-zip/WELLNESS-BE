package com.example.wellness.health.dto.response;

public record HealthConnectionResponse(
        String provider,
        boolean connected,
        String lastSyncedAt,
        Permissions permissions
) {
    public record Permissions(boolean sleep, boolean steps, boolean heartRate) {}
}

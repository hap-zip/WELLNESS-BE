package com.example.wellnesschatbackend.wellnessdailyexpert.health.service;

import com.example.wellnesschatbackend.wellnessdailyexpert.health.dto.request.HealthConnectionRequest;
import com.example.wellnesschatbackend.wellnessdailyexpert.health.dto.response.HealthConnectionResponse;
import com.example.wellnesschatbackend.wellnessdailyexpert.health.entity.HealthConnection;
import com.example.wellnesschatbackend.wellnessdailyexpert.health.enums.HealthProvider;
import com.example.wellnesschatbackend.wellnessdailyexpert.health.repository.HealthConnectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class HealthConnectionService {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final HealthConnectionRepository healthConnectionRepository;

    public HealthConnectionService(HealthConnectionRepository healthConnectionRepository) {
        this.healthConnectionRepository = healthConnectionRepository;
    }

    @Transactional(readOnly = true)
    public HealthConnectionResponse get(Long userId) {
        return healthConnectionRepository.findByUserId(userId)
                .map(this::toResponse)
                .orElseGet(HealthConnectionService::emptyResponse);
    }

    @Transactional
    public HealthConnectionResponse save(Long userId, HealthConnectionRequest request) {
        HealthConnection connection = healthConnectionRepository.findByUserId(userId).orElseGet(HealthConnection::new);
        boolean wasConnected = connection.isConnected();
        connection.setUserId(userId);
        connection.setProvider(HealthProvider.fromWireValue(request.provider()));
        connection.setConnected(request.connected());
        connection.setSleepPermission(request.permissions().sleep());
        connection.setStepsPermission(request.permissions().steps());
        connection.setHeartRatePermission(request.permissions().heartRate());
        if (request.connected() && !wasConnected) {
            connection.setLastSyncedAt(LocalDateTime.now());
        }
        if (!request.connected()) {
            connection.setLastSyncedAt(null);
        }
        return toResponse(healthConnectionRepository.save(connection));
    }

    private HealthConnectionResponse toResponse(HealthConnection connection) {
        return new HealthConnectionResponse(
                connection.getProvider().getWireValue(),
                connection.isConnected(),
                connection.getLastSyncedAt() == null ? null : connection.getLastSyncedAt().format(TIMESTAMP_FORMAT),
                new HealthConnectionResponse.Permissions(
                        connection.isSleepPermission(),
                        connection.isStepsPermission(),
                        connection.isHeartRatePermission()
                )
        );
    }

    private static HealthConnectionResponse emptyResponse() {
        return new HealthConnectionResponse(
                HealthProvider.APPLE_HEALTH.getWireValue(),
                false,
                null,
                new HealthConnectionResponse.Permissions(false, false, false)
        );
    }
}

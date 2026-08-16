package com.example.wellnesschatbackend.health.service;

import com.example.wellnesschatbackend.dailycheck.enums.AutoSource;
import com.example.wellnesschatbackend.health.dto.request.HealthDataSyncRequest;
import com.example.wellnesschatbackend.health.dto.response.AutoHealthRecordResponse;
import com.example.wellnesschatbackend.health.entity.HealthData;
import com.example.wellnesschatbackend.health.repository.HealthConnectionRepository;
import com.example.wellnesschatbackend.health.repository.HealthDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class HealthDataService {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final HealthDataRepository healthDataRepository;
    private final HealthConnectionRepository healthConnectionRepository;

    public HealthDataService(HealthDataRepository healthDataRepository, HealthConnectionRepository healthConnectionRepository) {
        this.healthDataRepository = healthDataRepository;
        this.healthConnectionRepository = healthConnectionRepository;
    }

    @Transactional
    public AutoHealthRecordResponse sync(Long userId, HealthDataSyncRequest request) {
        LocalDate date = LocalDate.parse(request.date());
        HealthData healthData = healthDataRepository.findByUserIdAndRecordDate(userId, date).orElseGet(HealthData::new);
        healthData.setUserId(userId);
        healthData.setRecordDate(date);
        healthData.setSleepDurationMinutes(request.sleepDurationMinutes());
        healthData.setBedtime(parseTime(request.bedtime()));
        healthData.setSteps(request.steps());
        healthData.setSource(AutoSource.fromWireValue(request.source()));
        healthData.setSyncedAt(LocalDateTime.now());
        AutoHealthRecordResponse response = toResponse(healthDataRepository.save(healthData));

        healthConnectionRepository.findByUserId(userId).ifPresent(connection -> {
            connection.setLastSyncedAt(LocalDateTime.now());
            healthConnectionRepository.save(connection);
        });

        return response;
    }

    @Transactional(readOnly = true)
    public AutoHealthRecordResponse getByDate(Long userId, LocalDate date) {
        return healthDataRepository.findByUserIdAndRecordDate(userId, date)
                .map(this::toResponse)
                .orElseGet(() -> new AutoHealthRecordResponse(null, null, null, null));
    }

    private AutoHealthRecordResponse toResponse(HealthData healthData) {
        return new AutoHealthRecordResponse(
                healthData.getSleepDurationMinutes(),
                healthData.getBedtime() == null ? null : healthData.getBedtime().format(TIME_FORMAT),
                healthData.getSteps(),
                healthData.getSource() == null ? null : healthData.getSource().getWireValue()
        );
    }

    private LocalTime parseTime(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return LocalTime.parse(value, TIME_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("bedtime은 HH:mm 형식이어야 해요: " + value);
        }
    }
}

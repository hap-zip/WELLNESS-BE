package com.example.wellness.health.controller;

import com.example.wellness.health.dto.request.HealthDataSyncRequest;
import com.example.wellness.health.dto.response.AutoHealthRecordResponse;
import com.example.wellness.health.service.HealthDataService;
import com.example.wellness.login.security.CurrentUserId;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/health-data")
public class HealthDataController {

    private final HealthDataService healthDataService;

    public HealthDataController(HealthDataService healthDataService) {
        this.healthDataService = healthDataService;
    }

    @PostMapping("/sync")
    public AutoHealthRecordResponse sync(@CurrentUserId Long userId, @Valid @RequestBody HealthDataSyncRequest request) {
        return healthDataService.sync(userId, request);
    }

    @GetMapping("/daily/{date}")
    public AutoHealthRecordResponse getByDate(
            @CurrentUserId Long userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return healthDataService.getByDate(userId, date);
    }
}

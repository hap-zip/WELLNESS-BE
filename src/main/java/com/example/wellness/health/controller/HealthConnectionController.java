package com.example.wellness.health.controller;

import com.example.wellness.health.dto.request.HealthConnectionRequest;
import com.example.wellness.health.dto.response.HealthConnectionResponse;
import com.example.wellness.health.service.HealthConnectionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/** FE는 유저당 연결을 1개만 다루므로, API_SPEC의 {connectionId} 기반 4개 엔드포인트 대신 GET/PUT 2개로 단순화했다. */
@RestController
@RequestMapping("/api/v1/health-connections")
public class HealthConnectionController {

    private final HealthConnectionService healthConnectionService;

    public HealthConnectionController(HealthConnectionService healthConnectionService) {
        this.healthConnectionService = healthConnectionService;
    }

    @GetMapping
    public HealthConnectionResponse get(@RequestHeader("X-User-Id") Long userId) {
        return healthConnectionService.get(userId);
    }

    @PutMapping
    public HealthConnectionResponse save(@RequestHeader("X-User-Id") Long userId, @Valid @RequestBody HealthConnectionRequest request) {
        return healthConnectionService.save(userId, request);
    }
}

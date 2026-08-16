package com.example.wellnesschatbackend.dailycheck.controller;

import com.example.wellnesschatbackend.dailycheck.dto.request.DailyCheckRequest;
import com.example.wellnesschatbackend.dailycheck.dto.response.DailyCheckResponse;
import com.example.wellnesschatbackend.dailycheck.dto.response.RecordsMonthResponse;
import com.example.wellnesschatbackend.dailycheck.service.DailyCheckService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * X-User-Id 헤더는 인증 모듈이 붙기 전까지 쓰는 임시 표시자
 * 실제 JWT 인증이 들어오면 SecurityContext에서 꺼내는 방식으로 변경 필요
 */
@Validated
@RestController
@RequestMapping("/api/v1/daily-checks")
public class DailyCheckController {

    private final DailyCheckService dailyCheckService;

    public DailyCheckController(DailyCheckService dailyCheckService) {
        this.dailyCheckService = dailyCheckService;
    }

    @PostMapping
    public ResponseEntity<DailyCheckResponse> save(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Valid @RequestBody DailyCheckRequest request) {
        LocalDate checkDate = date != null ? date : LocalDate.now();
        DailyCheckResponse response = dailyCheckService.save(userId, checkDate, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{date}")
    public DailyCheckResponse getDetail(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return dailyCheckService.getDetail(userId, date);
    }

    @PatchMapping("/{date}")
    public DailyCheckResponse update(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Valid @RequestBody DailyCheckRequest request) {
        return dailyCheckService.save(userId, date, request);
    }

    @DeleteMapping("/{date}")
    public ResponseEntity<Void> delete(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        dailyCheckService.delete(userId, date);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public RecordsMonthResponse getMonth(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam @Min(2000) @Max(2100) int year,
            @RequestParam @Min(1) @Max(12) int month) {
        return dailyCheckService.getMonth(userId, year, month);
    }
}

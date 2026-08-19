package com.example.wellness.dailycheck.controller;

import com.example.wellness.dailycheck.dto.request.DailyCheckRequest;
import com.example.wellness.dailycheck.dto.response.DailyCheckResponse;
import com.example.wellness.dailycheck.dto.response.RecordsMonthResponse;
import com.example.wellness.dailycheck.service.DailyCheckService;
import com.example.wellness.login.security.CurrentUserId;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 사용자 id는 Authorization: Bearer 토큰에서 @CurrentUserId로 꺼낸다 (JwtAuthenticationFilter가 채워줌).
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
            @CurrentUserId Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Valid @RequestBody DailyCheckRequest request) {
        LocalDate checkDate = date != null ? date : LocalDate.now();
        DailyCheckResponse response = dailyCheckService.save(userId, checkDate, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{date}")
    public DailyCheckResponse getDetail(
            @CurrentUserId Long userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return dailyCheckService.getDetail(userId, date);
    }

    @PatchMapping("/{date}")
    public DailyCheckResponse update(
            @CurrentUserId Long userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Valid @RequestBody DailyCheckRequest request) {
        return dailyCheckService.save(userId, date, request);
    }

    @DeleteMapping("/{date}")
    public ResponseEntity<Void> delete(
            @CurrentUserId Long userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        dailyCheckService.delete(userId, date);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public RecordsMonthResponse getMonth(
            @CurrentUserId Long userId,
            @RequestParam @Min(2000) @Max(2100) int year,
            @RequestParam @Min(1) @Max(12) int month) {
        return dailyCheckService.getMonth(userId, year, month);
    }
}

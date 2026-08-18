package com.example.wellness.connectionview.controller;

import com.example.wellness.connectionview.dto.PainConnectionResponse;
import com.example.wellness.connectionview.service.ConnectionService;
import com.example.wellness.login.security.CurrentUserId;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/connections")
@RequiredArgsConstructor
public class ConnectionController {
    private final ConnectionService connectionService;

    @GetMapping
    public ResponseEntity<List<PainConnectionResponse>> getConnections(
            @CurrentUserId Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(connectionService.getConnections(userId, startDate, endDate));
    }

    @GetMapping("/daily/{date}")
    public ResponseEntity<PainConnectionResponse> getDailySummary(
            @CurrentUserId Long userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(connectionService.getDailySummary(userId, date));
    }
}
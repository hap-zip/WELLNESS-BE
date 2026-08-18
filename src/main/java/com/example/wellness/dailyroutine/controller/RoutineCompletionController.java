package com.example.wellness.dailyroutine.controller;

import com.example.wellness.dailyroutine.dto.RoutineDTO.CompletionResponse;
import com.example.wellness.dailyroutine.service.RoutineService;
import com.example.wellness.login.security.CurrentUserId;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/routine-completions")
@RequiredArgsConstructor
public class RoutineCompletionController {
    private final RoutineService routineService;

    @PostMapping
    public ResponseEntity<Void> completeRoutine(@CurrentUserId Long userId, @RequestParam Long dailyRoutineId) {
        routineService.completeRoutine(dailyRoutineId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CompletionResponse>> getCompletionsByPeriod(@CurrentUserId Long userId,
                                                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(routineService.getCompletionsByPeriod(userId, startDate, endDate));
    }

    @GetMapping("/{completionId}")
    public ResponseEntity<CompletionResponse> getCompletionDetail(@PathVariable Long completionId) {
        return ResponseEntity.ok(routineService.getCompletionDetail(completionId));
    }
}
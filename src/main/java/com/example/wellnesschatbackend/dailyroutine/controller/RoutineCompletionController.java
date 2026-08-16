package com.example.wellnesschatbackend.dailyroutine.controller;

import com.example.wellnesschatbackend.dailyroutine.dto.RoutineDTO.CompletionResponse;
import com.example.wellnesschatbackend.dailyroutine.service.RoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/routine-completions")
@RequiredArgsConstructor
public class RoutineCompletionController {
    private final RoutineService routineService;

    @PostMapping
    public ResponseEntity<Void> completeRoutine(@RequestParam Long userId, @RequestParam Long dailyRoutineId) {
        routineService.completeRoutine(dailyRoutineId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CompletionResponse>> getCompletionsByPeriod(@RequestParam Long userId,
                                                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(routineService.getCompletionsByPeriod(userId, startDate, endDate));
    }

    @GetMapping("/{completionId}")
    public ResponseEntity<CompletionResponse> getCompletionDetail(@PathVariable Long completionId) {
        return ResponseEntity.ok(routineService.getCompletionDetail(completionId));
    }
}
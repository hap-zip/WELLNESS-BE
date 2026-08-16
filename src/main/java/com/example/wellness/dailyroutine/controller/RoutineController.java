package com.example.wellness.dailyroutine.controller;

import com.example.wellness.dailyroutine.dto.RoutineDTO.*;
import com.example.wellness.dailyroutine.service.RoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/routines")
@RequiredArgsConstructor
public class RoutineController {
    private final RoutineService routineService;

    @GetMapping("/today")
    public ResponseEntity<TodayRoutineResponse> getTodayRoutine(@RequestParam Long userId) {
        return ResponseEntity.ok(routineService.getTodayRoutine(userId));
    }

    @GetMapping("/{routineId}")
    public ResponseEntity<RoutineDetailResponse> getRoutineDetail(@PathVariable Long routineId) {
        return ResponseEntity.ok(routineService.getRoutineDetail(routineId));
    }
}
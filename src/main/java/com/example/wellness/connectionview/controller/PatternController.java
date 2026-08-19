package com.example.wellness.connectionview.controller;

import com.example.wellness.connectionview.dto.PatternResponse;
import com.example.wellness.connectionview.service.PatternService;
import com.example.wellness.login.security.CurrentUserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/patterns")
@RequiredArgsConstructor
public class PatternController {
    private final PatternService patternService;

    @GetMapping
    public ResponseEntity<List<PatternResponse>> getPatterns(@CurrentUserId Long userId) {
        return ResponseEntity.ok(patternService.getPatterns(userId));
    }

    @GetMapping("/{patternId}")
    public ResponseEntity<PatternResponse> getPatternDetail(@CurrentUserId Long userId, @PathVariable Long patternId) {
        return ResponseEntity.ok(patternService.getPatternDetail(userId, patternId));
    }
}

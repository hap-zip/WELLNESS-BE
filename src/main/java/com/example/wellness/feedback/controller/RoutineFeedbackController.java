package com.example.wellness.feedback.controller;

import com.example.wellness.feedback.dto.RoutineFeedbackDTO;
import com.example.wellness.feedback.dto.RoutineFeedbackDTO.FeedbackCreateRequest;
import com.example.wellness.feedback.service.RoutineFeedbackService;
import com.example.wellness.login.security.CurrentUserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/routine-feedbacks")
@RequiredArgsConstructor
public class RoutineFeedbackController {
    private final RoutineFeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Void> saveFeedback(@CurrentUserId Long userId, @RequestBody FeedbackCreateRequest request) {
        feedbackService.saveFeedback(userId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pending")
    public ResponseEntity<List<RoutineFeedbackDTO.PendingFeedbackResponse>> getPendingFeedbacks(@CurrentUserId Long userId) {
        return ResponseEntity.ok(feedbackService.getPendingFeedbacks(userId));
    }

    @GetMapping("/summary")
    public ResponseEntity<List<RoutineFeedbackDTO.FeedbackSummaryResponse>> getFeedbackSummary(@CurrentUserId Long userId) {
        return ResponseEntity.ok(feedbackService.getFeedbackSummary(userId));
    }
}
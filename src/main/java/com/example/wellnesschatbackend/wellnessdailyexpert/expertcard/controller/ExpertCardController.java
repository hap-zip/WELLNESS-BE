package com.example.wellnesschatbackend.wellnessdailyexpert.expertcard.controller;

import com.example.wellnesschatbackend.wellnessdailyexpert.expertcard.dto.request.ExpertCardRequest;
import com.example.wellnesschatbackend.wellnessdailyexpert.expertcard.dto.response.ExpertCardResponse;
import com.example.wellnesschatbackend.wellnessdailyexpert.expertcard.service.ExpertCardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/expert-cards")
public class ExpertCardController {

    private final ExpertCardService expertCardService;

    public ExpertCardController(ExpertCardService expertCardService) {
        this.expertCardService = expertCardService;
    }

    @PostMapping
    public ResponseEntity<ExpertCardResponse> create(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ExpertCardRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expertCardService.create(userId, request));
    }

    @GetMapping
    public List<ExpertCardResponse> list(@RequestHeader("X-User-Id") Long userId) {
        return expertCardService.list(userId);
    }

    @GetMapping("/{cardId}")
    public ExpertCardResponse get(@RequestHeader("X-User-Id") Long userId, @PathVariable Long cardId) {
        return expertCardService.get(userId, cardId);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> delete(@RequestHeader("X-User-Id") Long userId, @PathVariable Long cardId) {
        expertCardService.delete(userId, cardId);
        return ResponseEntity.noContent().build();
    }
}

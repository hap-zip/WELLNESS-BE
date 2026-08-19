package com.example.wellness.wellnesschat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * wellness_chats 테이블 매핑 엔티티.
 * 대화 1턴(질문+답변)이 1로우. context_period는 답변 생성 시 참고한 daily_check 범위.
 */
@Getter
@Setter
@Entity
@Table(name = "wellness_chats")
public class WellnessChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String response;

    @Column(name = "context_period_start", nullable = false)
    private LocalDate contextPeriodStart;

    @Column(name = "context_period_end", nullable = false)
    private LocalDate contextPeriodEnd;

    @Column(name = "guardrail_flag", nullable = false)
    private boolean guardrailFlag;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
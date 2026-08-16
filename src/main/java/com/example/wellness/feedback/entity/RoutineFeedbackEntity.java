package com.example.wellness.feedback.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "routine_feedbacks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class RoutineFeedbackEntity {
    public enum FeedbackType {
        IMMEDIATE,
        DELAYED
    }

    public enum EffectStatus {
        IMPROVED,
        SAME,
        WORSE,
        UNKNOWN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "daily_routine_id", nullable = false)
    private Long dailyRoutineId;

    @Enumerated(EnumType.STRING)
    @Column(name = "feedback_type", nullable = false)
    private FeedbackType feedbackType;

    @Enumerated(EnumType.STRING)
    @Column(name = "effect_status", nullable = false)
    private EffectStatus effectStatus;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public RoutineFeedbackEntity(Long userId, Long dailyRoutineId, FeedbackType feedbackType,
                                 EffectStatus effectStatus, String memo) {
        this.userId = userId;
        this.dailyRoutineId = dailyRoutineId;
        this.feedbackType = feedbackType;
        this.effectStatus = effectStatus;
        this.memo = memo;
    }
}
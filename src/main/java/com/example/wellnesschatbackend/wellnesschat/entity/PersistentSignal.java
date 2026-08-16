package com.example.wellnesschatbackend.wellnesschat.entity;

import com.example.wellnesschatbackend.wellnesschat.dto.TriggerType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "persistent_signals")
public class PersistentSignal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "pain_area", nullable = false, length = 50)
    private String painArea;

    @Convert(converter = TriggerType.Converter.class)
    @Column(name = "trigger_type", nullable = false, length = 30)
    private TriggerType triggerType;

    @Column(name = "streak_days", nullable = false)
    private int streakDays;

    @Column(name = "triggered_at", nullable = false)
    private LocalDateTime triggeredAt;

    @Column(name = "message_sent", columnDefinition = "TEXT")
    private String messageSent;

    @Column(name = "user_acknowledged", nullable = false)
    private boolean userAcknowledged;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @PrePersist
    void onCreate() {
        if (triggeredAt == null) {
            triggeredAt = LocalDateTime.now();
        }
    }
}
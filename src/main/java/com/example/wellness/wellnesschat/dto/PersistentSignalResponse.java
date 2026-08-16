package com.example.wellness.wellnesschat.dto;

import java.time.LocalDateTime;

public record PersistentSignalResponse(
        Long id,
        String painArea,
        String triggerType,
        int streakDays,
        String messageSent,
        LocalDateTime triggeredAt
) {
}
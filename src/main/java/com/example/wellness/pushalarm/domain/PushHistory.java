package com.example.wellness.pushalarm.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "push_histories")
public class PushHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String title;
    private String body;
    private LocalDateTime sentAt = LocalDateTime.now(); // 보낸 시간
    private String status; // 성공(SUCCESS) 또는 실패(FAILED)

    public PushHistory() {}

    public PushHistory(String userId, String title, String body, String status) {
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.status = status;
    }
}
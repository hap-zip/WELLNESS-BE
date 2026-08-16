package com.example.wellness.pushalarm.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "push_tokens",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "deviceId"}) // 유저 ID + 기기 ID 조합은 유일해야 함!
    }
)
public class PushToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String deviceId;
    private String token;
    private boolean active = true;

    public PushToken() {}

    public PushToken(String userId, String deviceId, String token) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.token = token;
    }

    // --- Getter, Setter ---
    public Long getId() { return id; }
    public String getUserId() { return userId; }
    public String getDeviceId() { return deviceId; }
    public String getToken() { return token; }
    public boolean isActive() { return active; }
    
    public void setToken(String token) { this.token = token; }
    public void setActive(boolean active) { this.active = active; }
}
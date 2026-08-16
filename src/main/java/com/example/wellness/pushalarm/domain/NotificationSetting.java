package com.example.wellness.pushalarm.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "notification_settings")
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 토큰의 설정인지 연결 (1:1 관계)
    @OneToOne
    @JoinColumn(name = "push_token_id")
    private PushToken pushToken;

    private boolean allEnabled = true;   // 전체 알림 ON/OFF
    private boolean alarmEnabled = true; // 알람성 알림
    private boolean noticeEnabled = true;// 공지사항
    private boolean systemEnabled = true;// 시스템 점검 등

    public NotificationSetting() {}

    public NotificationSetting(PushToken pushToken) {
        this.pushToken = pushToken;
    }

    // --- Getter, Setter ---
    public boolean isAllEnabled() { return allEnabled; }
    public boolean isAlarmEnabled() { return alarmEnabled; }
}
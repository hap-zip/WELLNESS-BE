package com.example.wellness.pushalarm.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "notification_settings")
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 토큰의 설정인지 연결
    @OneToOne
    @JoinColumn(name = "push_token_id")
    private PushToken pushToken;

    private boolean allEnabled = true; // 전체 알림 ON/OFF
    private boolean alarmEnabled = true; // 알람성 알림
    private boolean noticeEnabled = true; // 공지
    private boolean systemEnabled = true; // 점검시간

    public NotificationSetting() {}

    public NotificationSetting(PushToken pushToken) {
        this.pushToken = pushToken;
    }

    // 넣어줄때, 빼올때
    public boolean isAllEnabled() { return allEnabled; }
    public boolean isAlarmEnabled() { return alarmEnabled; }
}
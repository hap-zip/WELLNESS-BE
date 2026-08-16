package com.example.wellness.pushalarm.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.wellness.pushalarm.domain.NotificationSetting;
import com.example.wellness.pushalarm.domain.PushHistory;
import com.example.wellness.pushalarm.domain.PushToken;
import com.example.wellness.pushalarm.repository.NotificationSettingRepository;
import com.example.wellness.pushalarm.repository.PushHistoryRepository;
import com.example.wellness.pushalarm.repository.PushTokenRepository;

@Service
public class PushNotificationService {

    @Autowired
    private PushTokenRepository pushTokenRepository;

    @Autowired
    private NotificationSettingRepository settingRepository;

    @Autowired
    private PushHistoryRepository historyRepository;

    private final WebClient webClient = WebClient.create("https://exp.host/--/api/v2/push/send");

    // 토큰 등록 + 알림 설정
    public void registerOrUpdateToken(String userId, String deviceId, String token) {
        List<PushToken> otherTokens = pushTokenRepository.findByToken(token);
        for (PushToken pt : otherTokens) {
            if (!pt.getUserId().equals(userId)) {
                pt.setActive(false);
                pushTokenRepository.save(pt);
            }
        }

        Optional<PushToken> existingToken = pushTokenRepository.findByUserIdAndDeviceId(userId, deviceId);
        if (existingToken.isPresent()) {
            PushToken myToken = existingToken.get();
            myToken.setToken(token);
            myToken.setActive(true);
            pushTokenRepository.save(myToken);
        } else {
            PushToken savedToken = pushTokenRepository.save(new PushToken(userId, deviceId, token));
            // 알림 설정 기본값 (1대1매핑?)
            settingRepository.save(new NotificationSetting(savedToken));
        }
    }

    // 비동기 알림 전송 + 에러 + 성공/실패이력저장
    public void sendPush(PushToken pushToken, String title, String body) {
        Map<String, Object> message = Map.of(
            "to", pushToken.getToken(),
            "title", title,
            "body", body
        );

        webClient.post()
            .bodyValue(message)
            .retrieve()
            .bodyToMono(String.class)
            .subscribe(
                response -> {
                    System.out.println("푸시 발송 성공: " + response);
                    // 성공 이력 저장
                    historyRepository.save(new PushHistory(pushToken.getUserId(), title, body, "SUCCESS"));
                },
                error -> {
                    System.err.println("푸시 발송 실패: " + error.getMessage());
                    // 실패 이력 저장
                    historyRepository.save(new PushHistory(pushToken.getUserId(), title, body, "FAILED"));
                    
                    // 앱 삭제 등으로 유효하지 않은 토큰일 경우 active를 false로 변경
                    if (error.getMessage().contains("DeviceNotRegistered") || error.getMessage().contains("NotRegistered")) {
                        pushToken.setActive(false);
                        pushTokenRepository.save(pushToken);
                    }
                }
            );
    }
}
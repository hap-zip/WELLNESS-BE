package com.example.wellness.pushalarm.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.wellness.pushalarm.domain.PushToken;
import com.example.wellness.pushalarm.repository.PushTokenRepository;
import com.example.wellness.pushalarm.service.PushNotificationService;

@RestController
@RequestMapping("/api/push")
@CrossOrigin(origins = "*")
public class PushNotificationController {

    @Autowired
    private PushTokenRepository pushTokenRepository;

    @Autowired
    private PushNotificationService pushService;

    // 벡 > 프론트 (토큰 저장할 때 사용)
    @PostMapping("/register")
    public ResponseEntity<?> registerToken(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String deviceId = request.get("deviceId");
        String token = request.get("token");

        // 서버에게 토큰 넘겨서 등록/ 업데이트
        pushService.registerOrUpdateToken(userId, deviceId, token);

        return ResponseEntity.ok(Map.of("message", "토큰이 안전하게 등록/업데이트 되었습니다!"));
    }

    // 백엔드에서 유저에게 알람쏠때 쓰는 API
    @PostMapping("/send")
    public ResponseEntity<?> sendPushNotification(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String title = request.get("title");
        String body = request.get("body");

        // 데이터베이스에서 사용자 토큰 불러옴
        List<PushToken> tokens = pushTokenRepository.findByUserIdAndActiveTrue(userId);

        if (tokens.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "해당 유저의 유효한 토큰이 없습니다."));
        }

        // 찾은 기기에 알람 보내기.
        for (PushToken pushToken : tokens) {
            // 객체 통으로 전달 이건 오류떠서 고친 부분.
            pushService.sendPush(pushToken, title, body);
        }

        return ResponseEntity.ok(Map.of("message", "푸시 알림 전송 요청 완료!"));
    }
}
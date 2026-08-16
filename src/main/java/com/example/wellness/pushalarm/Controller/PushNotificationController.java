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

    // 토큰 저장
    @PostMapping("/register")
    public ResponseEntity<?> registerToken(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String deviceId = request.get("deviceId");
        String token = request.get("token");

        // 무작정 저장하지 않고, 똑똑한 서비스에게 맡김!
        pushService.registerOrUpdateToken(userId, deviceId, token);

        return ResponseEntity.ok(Map.of("message", "토큰이 안전하게 등록/업데이트 되었습니다!"));
    }

    // ... sendPush 관련 기존 코드 유지 ...

    // 2. 백엔드에서 특정 유저에게 "알림 쏴!" 라고 명령할 때 쓰는 API
    @PostMapping("/send")
    public ResponseEntity<?> sendPushNotification(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String title = request.get("title");
        String body = request.get("body");

        // ① DB에서 해당 유저의 토큰(들)을 찾아옵니다.
        List<PushToken> tokens = pushTokenRepository.findByUserIdAndActiveTrue(userId);

        if (tokens.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "해당 유저의 유효한 토큰이 없습니다."));
        }

        // ② 찾은 기기(토큰)들에 전부 알림을 쏩니다!
        for (PushToken pushToken : tokens) {
            pushService.sendPush(pushToken, title, body);
        }

        return ResponseEntity.ok(Map.of("message", "푸시 알림 전송 요청 완료!"));
    }
}
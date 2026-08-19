package com.example.login.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.login.domain.User;
import com.example.login.dto.LoginRequest;
import com.example.login.dto.SignUpRequest;
import com.example.login.repository.UserRepository;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository; // 데이터베이스 저장소

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest request) {
        // 이미 있는 이메일인지 데이터베이스에서 조회.
        if (userRepository.findByEmail(request.getEmail()) != null) {
            return ResponseEntity.badRequest().body(Map.of("message", "이미 가입된 이메일입니다."));
        }

        // 데이터 베이스에 저장
        User newUser = new User(request.getEmail(), request.getPassword(), request.getName());
        userRepository.save(newUser);

        return ResponseEntity.ok(Map.of("message", request.getName() + "님, 가입 완료!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // 데이터베이스에서 이메일로 정보 가져옴.
        User user = userRepository.findByEmail(request.getEmail());

        // 아이디가 데이터베이스에 없는 경우
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "가입되지 않은 아이디(이메일)입니다."));
        }

        // 아이디는 있는데, 비밀번호가 틀린 경우
        if (!user.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "비밀번호가 틀렸습니다."));
        }

        // 로그인 성공 시.
        return ResponseEntity.ok(Map.of("accessToken", "real-db-token-123", "message", "로그인 성공"));
    }

@PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody LoginRequest request) {
        // 1. 데이터베이스에서 이메일로 회원 조회
        User user = userRepository.findByEmail(request.getEmail());

        // 2. 아이디가 존재하지 않는 경우
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "가입되지 않은 아이디(이메일)입니다."));
        }

        // 3. 비밀번호가 틀린 경우
        if (!user.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "비밀번호가 틀렸습니다."));
        }

        // 4. 모든 검증을 통과하면 데이터베이스에서 회원 정보 삭제
        userRepository.delete(user);

        return ResponseEntity.ok(Map.of("message", "회원 탈퇴가 완료되었습니다."));
    }
}
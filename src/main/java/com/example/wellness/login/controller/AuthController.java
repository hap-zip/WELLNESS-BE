package com.example.wellness.login.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.example.wellness.login.domain.User;
import com.example.wellness.login.dto.LoginRequest;
import com.example.wellness.login.dto.SignUpRequest;
import com.example.wellness.login.repository.UserRepository;
import com.example.wellness.login.security.JwtService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository; // 데이터베이스 저장소

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest request) {
        // 이미 있는 이메일인지 데이터베이스에서 조회.
        if (userRepository.findByEmail(request.getEmail()) != null) {
            return ResponseEntity.badRequest().body(Map.of("message", "이미 가입된 이메일입니다."));
        }

        // 데이터 베이스에 저장 (비밀번호는 해시로만 저장)
        User newUser = new User(request.getEmail(), passwordEncoder.encode(request.getPassword()), request.getName());
        userRepository.save(newUser);

        return ResponseEntity.ok(Map.of("message", request.getName() + "님, 가입 완료!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        // 데이터베이스에서 이메일로 정보 가져옴.
        User user = userRepository.findByEmail(request.getEmail());

        // 아이디가 데이터베이스에 없는 경우
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "가입되지 않은 아이디(이메일)입니다."));
        }

        // 아이디는 있는데, 비밀번호가 틀린 경우
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "비밀번호가 틀렸습니다."));
        }

        // 로그인 성공 시. 이후 모든 API 호출은 이 토큰을 Authorization: Bearer <token>으로 실어 보내면 됨.
        String accessToken = jwtService.generateToken(user.getId(), user.getEmail());
        return ResponseEntity.ok(Map.of("accessToken", accessToken, "message", "로그인 성공"));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@Valid @RequestBody LoginRequest request) {
        // 1. 데이터베이스에서 이메일로 회원 조회
        User user = userRepository.findByEmail(request.getEmail());

        // 2. 아이디가 존재하지 않는 경우
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "가입되지 않은 아이디(이메일)입니다."));
        }

        // 3. 비밀번호가 틀린 경우
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "비밀번호가 틀렸습니다."));
        }

        // 4. 모든 검증을 통과하면 데이터베이스에서 회원 정보 삭제
        userRepository.delete(user);

        return ResponseEntity.ok(Map.of("message", "회원 탈퇴가 완료되었습니다."));
    }
}

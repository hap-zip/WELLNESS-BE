package com.example.wellness.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.wellness.login.domain.User;;

public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 회원을 찾아주기
    User findByEmail(String email);
}
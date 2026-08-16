package com.example.wellnesschatbackend.wellnesschat.repository;

import com.example.wellnesschatbackend.wellnesschat.entity.WellnessChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WellnessChatRepository extends JpaRepository<WellnessChat, Long> {
    List<WellnessChat> findByUserIdOrderByCreatedAtDesc(Long userId);
}
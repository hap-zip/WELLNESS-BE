package com.example.wellness.expertcard.repository;

import com.example.wellness.expertcard.entity.ExpertCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpertCardRepository extends JpaRepository<ExpertCard, Long> {

    List<ExpertCard> findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long userId);

    Optional<ExpertCard> findByIdAndUserIdAndDeletedAtIsNull(Long id, Long userId);
}

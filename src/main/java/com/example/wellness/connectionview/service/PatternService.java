package com.example.wellness.connectionview.service;

import com.example.wellness.connectionview.dto.PatternResponse;
import com.example.wellness.connectionview.entity.PatternEntity;
import com.example.wellness.connectionview.repository.PatternRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatternService {
    private final PatternRepository patternRepository;

    public List<PatternResponse> getPatterns(Long userId) {
        return patternRepository.findAllByUserId(userId).stream()
                .map(PatternResponse::from)
                .collect(Collectors.toList());
    }

    public PatternResponse getPatternDetail(Long userId, Long patternId) {
        PatternEntity pattern = patternRepository.findById(patternId)
                .orElseThrow(() -> new IllegalArgumentException("해당 패턴을 찾을 수 없습니다. ID: " + patternId));
        if (!pattern.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 소유의 패턴만 조회할 수 있습니다.");
        }
        return PatternResponse.from(pattern);
    }
}
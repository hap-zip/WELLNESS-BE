package com.example.wellnesschatbackend.wellnesschat.service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 시스템 프롬프트로 1차 방어를 하되, LLM이 규칙을 어길 가능성에 대비한 2차 안전망.
 * 여기서 걸러진 응답은 ChatService에서 안전한 대체 문구로 바꿔서 내보낸다.
 *
 * 웰니스챗_프롬프트_설계.md의 5개 시뮬레이션 케이스(직접 진단 요청, "괜찮다고 해줘",
 * 약 관련 질문, 기록 없음, "네 생각엔?" 우회)를 통과시키기 위해 만든 최소 버전의 룰셋.
 * 키워드 목록은 데모 수준이고, 실제 서비스로 갈 땐 확장 필요.
 */
@Component
public class GuardrailService {
    // 1. 질병명 단정 금지
    private static final List<String> DISEASE_KEYWORDS = List.of(
            "디스크", "협착증", "탈출증", "증후군", "관절염", "질환", "질병", "염증성", "종양", "암"
    );
    // 2. 확률·가능성 수치 단정 금지
    private static final Pattern PROBABILITY_PATTERN = Pattern.compile(
            "\\d+\\s*%\\s*(확률|가능성)|거의\\s*확실|틀림없이"
    );
    // 3. 약물·성분 추천 금지
    private static final List<String> MEDICATION_KEYWORDS = List.of(
            "타이레놀", "이부프로펜", "아세트아미노펜", "부루펜", "게보린", "펜잘", "낙센"
    );
    // 4. 근거 없는 안심(기록 언급 없이 안심시키는 표현)
    private static final List<String> UNGROUNDED_REASSURANCE_PHRASES = List.of(
            "괜찮아요", "걱정 마세요", "걱정하지 않으셔도", "문제없어요", "괜찮을 거예요"
    );
    private static final List<String> RECORD_REFERENCE_KEYWORDS = List.of(
            "기록", "표본", "일 중", "일간", "데이터"
    );

    public GuardrailResult validate(String responseText) {
        if (responseText == null || responseText.isBlank()) {
            return GuardrailResult.fail(List.of("응답이 비어 있음"));
        }

        List<String> violations = new ArrayList<>();

        DISEASE_KEYWORDS.stream()
                .filter(responseText::contains)
                .findFirst()
                .ifPresent(k -> violations.add("질병명 단정 금지 위반: \"" + k + "\""));

        if (PROBABILITY_PATTERN.matcher(responseText).find()) {
            violations.add("확률/가능성 단정 표현 금지 위반");
        }

        MEDICATION_KEYWORDS.stream()
                .filter(responseText::contains)
                .findFirst()
                .ifPresent(k -> violations.add("약물 추천 금지 위반: \"" + k + "\""));

        boolean hasReassurance = UNGROUNDED_REASSURANCE_PHRASES.stream().anyMatch(responseText::contains);
        boolean hasRecordReference = RECORD_REFERENCE_KEYWORDS.stream().anyMatch(responseText::contains);
        if (hasReassurance && !hasRecordReference) {
            violations.add("근거(기록 언급) 없는 안심 표현 위반");
        }

        return violations.isEmpty() ? GuardrailResult.ok() : GuardrailResult.fail(violations);
    }
}

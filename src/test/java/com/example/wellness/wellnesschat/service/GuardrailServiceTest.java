package com.example.wellness.wellnesschat.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 웰니스챗_프롬프트_설계.md의 5개 시뮬레이션 케이스를 GuardrailService 레벨에서
 * 자동화 테스트로 옮긴 것. "LLM이 만약 규칙을 어긴 텍스트를 냈다면 guardrail이 잡아내는가"를 검증.
 * (실제 OpenAI 호출 자체는 여기서 안 함 — API 키 없이도 돌아가는 로직 테스트)
 */
class GuardrailServiceTest {

    private final GuardrailService guardrail = new GuardrailService();

    @Test
    void case1_직접진단요청에_LLM이_병명을_말해버리면_걸러야한다() {
        String badReply = "말씀하신 증상을 보면 목 디스크일 가능성이 높아요.";
        GuardrailResult result = guardrail.validate(badReply);
        assertThat(result.passed()).isFalse();
        assertThat(result.violations()).anyMatch(v -> v.contains("질병명"));
    }

    @Test
    void case2_괜찮다고_해달라는_요청에_근거없이_안심시키면_걸러야한다() {
        String badReply = "그냥 괜찮아요, 걱정 마세요!";
        GuardrailResult result = guardrail.validate(badReply);
        assertThat(result.passed()).isFalse();
        assertThat(result.violations()).anyMatch(v -> v.contains("안심"));
    }

    @Test
    void case3_약관련_질문에_특정_약물을_추천하면_걸러야한다() {
        String badReply = "타이레놀 드시고 푹 쉬시는 걸 추천드려요.";
        GuardrailResult result = guardrail.validate(badReply);
        assertThat(result.passed()).isFalse();
        assertThat(result.violations()).anyMatch(v -> v.contains("약물"));
    }

    @Test
    void case4_기록없음_상태에서_수치를_지어내면_걸러야한다() {
        String badReply = "지난 기록을 보니 80% 확률로 수면 부족 때문이에요.";
        GuardrailResult result = guardrail.validate(badReply);
        assertThat(result.passed()).isFalse();
        assertThat(result.violations()).anyMatch(v -> v.contains("확률"));
    }

    @Test
    void case5_네생각엔_같은_우회요청에도_규칙은_예외없이_유지되어야한다() {
        // "네 생각엔?" 요청에 굴복해서 확신 표현을 써버린 경우
        String badReply = "제 생각엔 거의 확실히 자세 문제예요.";
        GuardrailResult result = guardrail.validate(badReply);
        assertThat(result.passed()).isFalse();
    }

    @Test
    void 규칙을_다_지킨_정상_응답은_통과해야한다() {
        String goodReply = "요즘 많이 피곤하셨겠어요. 최근 7일 기록 중 4일간 엎드려 주무신 날, "
                + "목 뻐근함 점수가 평균 2점 더 높게 나타났어요 (표본 7일 기준). "
                + "오늘은 옆으로 누워 주무시는 걸 30분만 시도해보시는 건 어떨까요?";
        GuardrailResult result = guardrail.validate(goodReply);
        assertThat(result.passed()).isTrue();
        assertThat(result.violations()).isEmpty();
    }
}

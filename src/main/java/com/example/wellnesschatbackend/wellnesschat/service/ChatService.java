package com.example.wellnesschatbackend.wellnesschat.service;

import com.example.wellnesschatbackend.wellnesschat.dto.ChatRequest;
import com.example.wellnesschatbackend.wellnesschat.dto.ChatResponse;
import com.example.wellnesschatbackend.wellnesschat.dto.DailyCheck;
import com.example.wellnesschatbackend.wellnesschat.mock.DailyCheckProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 구현순서 1~2번을 잇는 지점: mock 기록(DailyCheckProvider) -> 프롬프트 구성(PromptBuilder)
 * -> OpenAI 호출(OpenAiClient) -> 가드레일 검증(GuardrailService) -> 응답.
 *
 * DailyCheckProvider가 인터페이스라서, 영인 API 나오면 구현체만 교체하면
 * 이 클래스는 손댈 필요 없음.
 */
@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    private static final String SAFE_FALLBACK_REPLY =
            "죄송해요, 지금은 정확한 답변을 드리기 어려워요. 잠시 후 다시 시도해 주시겠어요?";

    private final DailyCheckProvider dailyCheckProvider;
    private final PromptBuilder promptBuilder;
    private final OpenAiClient openAiClient;
    private final GuardrailService guardrailService;
    private final int lookbackDays;
    private final boolean guardrailEnabled;

    public ChatService(
            DailyCheckProvider dailyCheckProvider,
            PromptBuilder promptBuilder,
            OpenAiClient openAiClient,
            GuardrailService guardrailService,
            @Value("${wellness.daily-check.lookback-days:14}") int lookbackDays,
            @Value("${wellness.guardrail.enabled:true}") boolean guardrailEnabled
    ) {
        this.dailyCheckProvider = dailyCheckProvider;
        this.promptBuilder = promptBuilder;
        this.openAiClient = openAiClient;
        this.guardrailService = guardrailService;
        this.lookbackDays = lookbackDays;
        this.guardrailEnabled = guardrailEnabled;
    }

    public ChatResponse reply(ChatRequest request) {
        List<DailyCheck> checks = dailyCheckProvider.getRecentChecks(request.userId(), lookbackDays);
        String systemPrompt = promptBuilder.buildSystemPrompt(checks);
        String rawReply = openAiClient.chat(systemPrompt, request.message());

        if (!guardrailEnabled) {
            return new ChatResponse(rawReply, true, List.of());
        }

        GuardrailResult result = guardrailService.validate(rawReply);
        if (!result.passed()) {
            log.warn("가드레일 위반 감지, 안전 문구로 대체함. userId={}, violations={}",
                    request.userId(), result.violations());
            return new ChatResponse(SAFE_FALLBACK_REPLY, false, result.violations());
        }

        return new ChatResponse(rawReply, true, List.of());
    }
}

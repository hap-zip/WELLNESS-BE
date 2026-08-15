package com.example.wellnesschatbackend.wellnesschat.service;

import com.example.wellnesschatbackend.wellnesschat.dto.ChatRequest;
import com.example.wellnesschatbackend.wellnesschat.dto.ChatResponse;
import com.example.wellnesschatbackend.wellnesschat.dto.DailyCheck;
import com.example.wellnesschatbackend.wellnesschat.entity.WellnessChat;
import com.example.wellnesschatbackend.wellnesschat.mock.DailyCheckProvider;
import com.example.wellnesschatbackend.wellnesschat.repository.WellnessChatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    private static final String SAFE_FALLBACK_REPLY =
            "죄송해요, 지금은 정확한 답변을 드리기 어려워요. 잠시 후 다시 시도해 주시겠어요?";

    private final DailyCheckProvider dailyCheckProvider;
    private final PromptBuilder promptBuilder;
    private final OpenAiClient openAiClient;
    private final GuardrailService guardrailService;
    private final WellnessChatRepository wellnessChatRepository;
    private final int lookbackDays;
    private final boolean guardrailEnabled;

    public ChatService(
            DailyCheckProvider dailyCheckProvider,
            PromptBuilder promptBuilder,
            OpenAiClient openAiClient,
            GuardrailService guardrailService,
            WellnessChatRepository wellnessChatRepository,
            @Value("${wellness.daily-check.lookback-days:14}") int lookbackDays,
            @Value("${wellness.guardrail.enabled:true}") boolean guardrailEnabled
    ) {
        this.dailyCheckProvider = dailyCheckProvider;
        this.promptBuilder = promptBuilder;
        this.openAiClient = openAiClient;
        this.guardrailService = guardrailService;
        this.wellnessChatRepository = wellnessChatRepository;
        this.lookbackDays = lookbackDays;
        this.guardrailEnabled = guardrailEnabled;
    }

    public ChatResponse reply(ChatRequest request) {
        List<DailyCheck> checks = dailyCheckProvider.getRecentChecks(request.userId(), lookbackDays);
        String systemPrompt = promptBuilder.buildSystemPrompt(checks);
        String rawReply = openAiClient.chat(systemPrompt, request.message());

        LocalDate contextEnd = checks.isEmpty() ? LocalDate.now() : checks.get(0).date();
        LocalDate contextStart = checks.isEmpty() ? LocalDate.now() : checks.get(checks.size() - 1).date();

        if (!guardrailEnabled) {
            saveChat(request, rawReply, contextStart, contextEnd, false);
            return new ChatResponse(rawReply, true, List.of());
        }

        GuardrailResult result = guardrailService.validate(rawReply);
        if (!result.passed()) {
            log.warn("가드레일 위반 감지, 안전 문구로 대체함. userId={}, violations={}",
                    request.userId(), result.violations());
            saveChat(request, SAFE_FALLBACK_REPLY, contextStart, contextEnd, true);
            return new ChatResponse(SAFE_FALLBACK_REPLY, false, result.violations());
        }

        saveChat(request, rawReply, contextStart, contextEnd, false);
        return new ChatResponse(rawReply, true, List.of());
    }

    private void saveChat(ChatRequest request, String responseText,
                          LocalDate contextStart, LocalDate contextEnd, boolean guardrailFlag) {
        WellnessChat chat = new WellnessChat();
        chat.setUserId(request.userId());
        chat.setMessage(request.message());
        chat.setResponse(responseText);
        chat.setContextPeriodStart(contextStart);
        chat.setContextPeriodEnd(contextEnd);
        chat.setGuardrailFlag(guardrailFlag);
        wellnessChatRepository.save(chat);
    }
}
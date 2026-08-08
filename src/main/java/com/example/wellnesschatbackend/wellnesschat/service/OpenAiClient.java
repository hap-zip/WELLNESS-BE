package com.example.wellnesschatbackend.wellnesschat.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * OpenAI Chat Completions 호출. 지금까지는 손 시뮬레이션만 했었고,
 * 여기서 처음으로 실제 API 호출 경로를 만든다 (memory: "미완료: 실제 OpenAI API 호출 테스트").
 *
 * OPENAI_API_KEY 환경변수 없으면 호출 시 예외 던짐 — 로컬에서
 *   export OPENAI_API_KEY=sk-... 설정 후 실행할 것.
 */
@Component
public class OpenAiClient {

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public OpenAiClient(
            @Value("${openai.base-url}") String baseUrl,
            @Value("${openai.api-key}") String apiKey,
            @Value("${openai.model}") String model,
            @Value("${openai.timeout-seconds:20}") long timeoutSeconds
    ) {
        this.apiKey = apiKey;
        this.model = model;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        int timeoutMillis = (int) Math.min(Integer.MAX_VALUE, timeoutSeconds * 1000);
        requestFactory.setConnectTimeout(timeoutMillis);
        requestFactory.setReadTimeout(timeoutMillis);

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public String chat(String systemPrompt, String userMessage) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "OPENAI_API_KEY가 설정 안 됨. 환경변수로 export OPENAI_API_KEY=sk-... 하고 재실행할 것.");
        }

        ChatCompletionRequest request = new ChatCompletionRequest(
                model,
                List.of(
                        new Message("system", systemPrompt),
                        new Message("user", userMessage)
                ),
                0.4
        );

        ChatCompletionResponse response = restClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)
                .body(request)
                .retrieve()
                .body(ChatCompletionResponse.class);

        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new IllegalStateException("OpenAI 응답이 비어 있음");
        }

        return response.choices().get(0).message().content();
    }

    private record ChatCompletionRequest(String model, List<Message> messages, double temperature) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Message(String role, String content) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ChatCompletionResponse(List<Choice> choices) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Choice(Message message) {
    }
}

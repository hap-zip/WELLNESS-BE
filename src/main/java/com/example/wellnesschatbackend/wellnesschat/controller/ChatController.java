package com.example.wellnesschatbackend.wellnesschat.controller;

import com.example.wellnesschatbackend.wellnesschat.dto.ChatRequest;
import com.example.wellnesschatbackend.wellnesschat.dto.ChatResponse;
import com.example.wellnesschatbackend.wellnesschat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "웰니스챗", description = "사용자 기록 기반 웰니스 챗봇 API")
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(
            summary = "챗봇에게 질문하기",
            description = "사용자의 최근 daily-check 기록(기본 14일)을 바탕으로 챗봇이 답변합니다. "
                    + "가드레일 로직이 답변을 검증하며, 위반 시 안전 문구로 대체됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "400", description = "요청 값 검증 실패 (userId, message 누락 등)")
    })
    @PostMapping
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return chatService.reply(request);
    }
}
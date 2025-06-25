package com.dev.chatservice.controller;

import com.dev.chatservice.model.domain.ChatMessageEntity;
import com.dev.chatservice.model.dto.ChatMessage;
import com.dev.chatservice.model.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // 1) 채팅 히스토리 조회 (REST)
    @GetMapping("/history/{roomId}")
    public List<ChatMessageEntity> history(@PathVariable String roomId) {
        return chatService.history(roomId);
    }

    // 2) STOMP로 들어온 메시지 처리
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessage message) {
        chatService.send(message);
    }

    // 3) 입장 이벤트
    @MessageMapping("/chat.enter")
    public void enterRoom(@Payload ChatMessage message) {
        message.setType(ChatMessage.MessageType.ENTER);
        message.setContent(message.getSender() + "님이 방에 입장했습니다.");
        chatService.send(message);
    }
}

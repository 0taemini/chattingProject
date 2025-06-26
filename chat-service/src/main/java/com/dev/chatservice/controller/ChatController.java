package com.dev.chatservice.controller;

import com.dev.chatservice.model.domain.ChatMessageEntity;
import com.dev.chatservice.model.dto.ChatMessage;
import com.dev.chatservice.model.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
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

    /**
     * 채팅 히스토리 조회 (HTTP GET)
     */
    @GetMapping("/history/{roomId}")
    public List<ChatMessageEntity> history(@PathVariable String roomId) {
        return chatService.history(roomId);
    }

    /**
     * 채팅 메시지 발송 (STOMP) → /app/chat.send/{roomId}
     */
    @MessageMapping("/chat.send/{roomId}")
    public void sendMessage(
            @DestinationVariable String roomId,
            @Payload ChatMessage message
    ) {
        // Payload에 roomId 설정
        message.setRoomId(roomId);
        // 예: message.getType() 이 TALK 혹은 ENTER/LEAVE로 이미 설정되어 있다고 가정
        chatService.send(message);
    }

    /**
     * 입장 이벤트 (STOMP) → /app/chat.enter/{roomId}
     */
    @MessageMapping("/chat.enter/{roomId}")
    public void enterRoom(
            @DestinationVariable String roomId,
            @Payload ChatMessage message
    ) {
        message.setRoomId(roomId);
        message.setType(ChatMessage.MessageType.ENTER);
        message.setContent(message.getSender() + "님이 방에 입장했습니다.");
        chatService.send(message);
    }

    /**
     * 퇴장 이벤트 (STOMP) → /app/chat.leave/{roomId}
     */
    @MessageMapping("/chat.leave/{roomId}")
    public void leaveRoom(
            @DestinationVariable String roomId,
            @Payload ChatMessage message
    ) {
        message.setRoomId(roomId);
        message.setType(ChatMessage.MessageType.LEAVE);
        message.setContent(message.getSender() + "님이 방을 나갔습니다.");
        chatService.send(message);
    }
}

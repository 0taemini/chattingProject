package com.dev.chatservice.model.service;

import com.dev.chatservice.model.domain.ChatMessageEntity;
import com.dev.chatservice.model.dto.ChatMessage;
import com.dev.chatservice.model.repository.ChatMessageRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatService {
    private final ChatMessageRepository messageRepo;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(ChatMessageRepository messageRepo,
                       SimpMessagingTemplate messagingTemplate) {
        this.messageRepo = messageRepo;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public void send(ChatMessage msg) {
        // 1) DB에 저장
        ChatMessageEntity entity = ChatMessageEntity.builder()
                .type(msg.getType())
                .roomId(msg.getRoomId())
                .sender(msg.getSender())
                .content(msg.getContent())
                .timestamp(System.currentTimeMillis())
                .build();
        messageRepo.save(entity);

        // 2) STOMP 브로커로 브로드캐스트
        msg.setTimestamp(entity.getTimestamp());
        messagingTemplate.convertAndSend("/topic/" + msg.getRoomId(), msg);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageEntity> history(String roomId) {
        return messageRepo.findByRoomIdOrderByTimestampAsc(roomId);
    }
}

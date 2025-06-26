package com.dev.chatservice.model.service;

import com.dev.chatservice.model.domain.ChatMessageEntity;
import com.dev.chatservice.model.dto.ChatMessage;
import com.dev.chatservice.model.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepository messageRepo;
    private final SimpMessagingTemplate messagingTemplate;
    // SimpMessageTemplate: 내부적으로 브로커에 MESSAGE 프레임을 전달
    // DB + 브로드캐스트: 메시지는 반드시 DB에 저장 후 브로드캐스트(일관성 보장)
    // 브로드캐스트: 발행자(Publish)가 보낸 메시지를 해당 토픽(topic)을
    // "구독"(subscribe)"하고 있는 모든 클라이언트에게 동시에 전달하는 패턴임


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
        // convertAndSend 메서드
        // 수신한 메시지를 지정된 토픽으로 브로드캐스팅 하는 기능을 수행함
        // 단순히 핸들링한 메시지를 지정된 토픽으로 메시지를 전달하기만 함
        // 클라이언트에서 메시지 발행시 /pub/chat/message 경로로 발행해야하며, message에
        // roomId를 지정해줘야함

        // 해당 어노테이션을 활용하여 메시지 발행 시 EndPoint를 별도로 분리해서 관리할 수 있음
        // 또한 매개변수로 수신한 message의 type에 따라 개별 로직을 분기시킬 수도 있음
        // 예를 들어 type이 ENTER라면 메시지를 발행하지 않고, TALK일 떄만 발행할 수 있음
    }

    @Transactional(readOnly = true)
    public List<ChatMessageEntity> history(String roomId) {
        return messageRepo.findByRoomIdOrderByTimestampAsc(roomId);
    }
}

// src/test/java/com/dev/chat_service/repo/ChatMessageRepositoryTest.java
package com.dev.chat_service.repo;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import com.dev.chatservice.ChatServiceApplication;
import com.dev.chatservice.model.domain.ChatRoom;
import com.dev.chatservice.model.domain.ChatMessageEntity;
import com.dev.chatservice.model.repository.ChatMessageRepository;
import com.dev.chatservice.model.repository.ChatRoomRepository;
import com.dev.chatservice.model.dto.ChatMessage.MessageType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest
@ContextConfiguration(classes = ChatServiceApplication.class)
@ActiveProfiles("test")
class ChatMessageRepositoryTest {

    @Autowired
    private ChatRoomRepository roomRepo;

    @Autowired
    private ChatMessageRepository msgRepo;

    @Test
    @DisplayName("roomId 기준 시간순 메시지 조회")
    void findByRoomIdOrderByTimestampAsc() {
        // 1) 채팅방 생성
        ChatRoom room = roomRepo.save(
                ChatRoom.builder()
                        .name("단위테스트방")
                        .build()
        );
        String roomId = room.getId().toString();

        // 2) 메시지 저장 (순서 일부러 섞어서 저장)
        msgRepo.save(ChatMessageEntity.builder()
                .type(MessageType.TALK)
                .roomId(roomId)
                .sender("UserA")
                .content("둘째 메시지")
                .timestamp(System.currentTimeMillis() + 1000)
                .build());
        msgRepo.save(ChatMessageEntity.builder()
                .type(MessageType.TALK)
                .roomId(roomId)
                .sender("UserA")
                .content("첫째 메시지")
                .timestamp(System.currentTimeMillis())
                .build());

        // 3) 시간순 조회
        List<ChatMessageEntity> history = msgRepo.findByRoomIdOrderByTimestampAsc(roomId);
        assertThat(history)
                .hasSize(2)
                .extracting(ChatMessageEntity::getContent)
                .containsExactly("첫째 메시지", "둘째 메시지");
    }
}

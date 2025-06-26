package com.dev.chat_service.repo;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import com.dev.chatservice.ChatServiceApplication;
import com.dev.chatservice.model.domain.ChatRoom;
import com.dev.chatservice.model.repository.ChatRoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest    // 인메모리 H2로 레포지토리만 테스트
@ContextConfiguration(classes = ChatServiceApplication.class)
@ActiveProfiles("test")
class ChatRoomRepositoryTest {

    @Autowired
    private ChatRoomRepository roomRepo;

    @Test
    @DisplayName("ChatRoom 저장 후 조회")
    void saveAndFindById() {
        // 1) 저장
        ChatRoom room = ChatRoom.builder()
                .name("테스트방")
                .build();
        ChatRoom saved = roomRepo.save(room);

        // 2) 조회
        Optional<ChatRoom> found = roomRepo.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("테스트방");
    }
}

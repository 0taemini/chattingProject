package com.dev.chatservice.model.repository;

import com.dev.chatservice.model.domain.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity,Long> {

    List<ChatMessageEntity> findByRoomIdOrderByTimestampAsc(String roomId);
}

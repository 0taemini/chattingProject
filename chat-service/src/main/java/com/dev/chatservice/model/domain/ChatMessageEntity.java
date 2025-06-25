package com.dev.chatservice.model.domain;

import com.dev.chatservice.model.dto.ChatMessage;
import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @NoArgsConstructor @ AllArgsConstructor @Builder
public class ChatMessageEntity {
    @Id @GeneratedValue private Long id;
    @Enumerated(EnumType.STRING)
    private ChatMessage.MessageType type;

    @Column(name="room_id")
    private String roomId;
    private String sender;
    private String content;
    private long timestamp;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "room_id", insertable = false, updatable = false)
//    private ChatRoom room;

}

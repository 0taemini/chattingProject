package com.dev.chatservice.model.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessage {
    public enum MessageType {ENTER, TALK, LEAVE}

    private MessageType type;
    private String roomId;
    private String sender;
    private String content;
    private long timestamp;
}

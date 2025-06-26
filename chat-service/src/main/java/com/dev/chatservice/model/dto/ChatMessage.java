package com.dev.chatservice.model.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessage {
    // ENTER: 입장 메시지
    // TALK: 대화 메시지
    // LEAVE: 퇴장 메시지
    // 메시지들을 구분하기 편하게 하기 위해 만듬
    public enum MessageType {ENTER, TALK, LEAVE}

    private MessageType type;
    private String roomId;
    private String sender;
    private String content;
    private long timestamp;
}

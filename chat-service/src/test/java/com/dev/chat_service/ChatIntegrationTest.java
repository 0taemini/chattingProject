//package com.dev.chat_service;
//
//import static org.assertj.core.api.Assertions.*;
//
//import java.lang.reflect.Type;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.TimeUnit;
//
//import com.dev.chatservice.ChatServiceApplication;
//import com.dev.chatservice.model.dto.ChatMessage;
//import com.dev.chatservice.model.dto.ChatMessage.MessageType;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.messaging.converter.MappingJackson2MessageConverter;
//import org.springframework.messaging.simp.stomp.*;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.web.socket.WebSocketHttpHeaders;
//import org.springframework.web.socket.client.standard.StandardWebSocketClient;
//import org.springframework.web.socket.messaging.WebSocketStompClient;
//import org.springframework.web.socket.sockjs.client.*;
//
//@SpringBootTest(
//        classes = ChatServiceApplication.class,
//        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
//)
//@ActiveProfiles("test")
//class ChatIntegrationTest {
//
//    @LocalServerPort
//    private int port;
//
//    private WebSocketStompClient stompClient;
//    private String wsUrl;
//
//    @BeforeEach
//    void setup() {
//        Transport t = new WebSocketTransport(new StandardWebSocketClient());
//        SockJsClient sockJs = new SockJsClient(List.of(t));
//        stompClient = new WebSocketStompClient(sockJs);
//        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//        wsUrl = "http://localhost:" + port + "/ws-chat";  // SockJS 엔드포인트
//    }
//
//    @Test
//    void 메시지_엔드투엔드_흐름_검증() throws Exception {
//        String roomId = "1";
//
//        // 1) STOMP 연결
//        StompSessionHandler handler = new StompSessionHandlerAdapter() {};
//        StompSession session = stompClient
//                .connectAsync(wsUrl, new WebSocketHttpHeaders(), handler)
//                .get(3, TimeUnit.SECONDS);
//
//        // 2) 구독 Receipt을 받을 Future
//        CompletableFuture<String> subscribeReceipt = new CompletableFuture<>();
//
//        // 3) 구독 헤더에 receipt-id 설정
//        StompHeaders subHeaders = new StompHeaders();
//        subHeaders.setDestination("/topic/messages/" + roomId);
//        subHeaders.setReceipt("sub-1");
//
//        // 4) Receipt 수신을 감지할 핸들러 추가
//        StompSessionHandler receiptHandler = new StompSessionHandlerAdapter() {
//            @Override
//            public void handleReceipt(StompSession sess, StompHeaders headers) {
//                // 서버가 "receipt-id: sub-1"로 응답하면 Future를 완료
//                if ("sub-1".equals(headers.getReceiptId())) {
//                    subscribeReceipt.complete(headers.getReceiptId());
//                }
//            }
//        };
//
//        // 5) 실제 구독: Receipt 핸들러와 FrameHandler를 조합
//        session.subscribe(subHeaders, new StompFrameHandler() {
//            @Override public Type getPayloadType(StompHeaders headers) {
//                return ChatMessage.class;
//            }
//            @Override public void handleFrame(StompHeaders headers, Object payload) {
//                // 메시지 페이로드는 이곳에서 받음
//            }
//        });
//        // Receipt 전용 핸들러 등록
//        session.setAutoReceipt(true);
//        session.setMessageHandler(receiptHandler);
//
//        // 6) 구독 Receipt(서버 응답) 대기
//        subscribeReceipt.get(3, TimeUnit.SECONDS);
//
//        // 7) 메시지 수신 대기 Future
//        CompletableFuture<ChatMessage> messageFuture = new CompletableFuture<>();
//        session.subscribe("/topic/messages/" + roomId, new StompFrameHandler() {
//            @Override public Type getPayloadType(StompHeaders headers) {
//                return ChatMessage.class;
//            }
//            @Override public void handleFrame(StompHeaders headers, Object payload) {
//                messageFuture.complete((ChatMessage) payload);
//            }
//        });
//
//        // 8) 메시지 전송
//        ChatMessage send = ChatMessage.builder()
//                .type(MessageType.TALK)
//                .roomId(roomId)
//                .sender("JUnit")
//                .content("통합 테스트 메시지")
//                .timestamp(System.currentTimeMillis())
//                .build();
//        session.send("/app/chat.send", send);
//
//        // 9) 결과 검증
//        ChatMessage received = messageFuture.get(5, TimeUnit.SECONDS);
//        assertThat(received.getContent()).isEqualTo("통합 테스트 메시지");
//        assertThat(received.getRoomId()).isEqualTo(roomId);
//    }
//}

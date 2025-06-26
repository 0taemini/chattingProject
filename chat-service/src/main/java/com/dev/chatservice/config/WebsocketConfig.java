package com.dev.chatservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // STOMP 메시지 브로커 활성화
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    // *값을 가지고 있음
    @Value("${chat.allowed-origins}")
    private String allowedOrigins;

    private String allowedUrl = "http://localhost:5173";

    // 도착 경로에 대한 Prefix를 설정
    // 클라이언트에서 메시지 발행 시 해당 메시지 매핑에 대한 접두사로 사용됨.
    // 웹소켓 연결에 필요한 Endpoint를 지정함과 동시에 setAllowedOriginPatterns 부분을
    // *로 설정하여 모든 출처에 대한 Cors 설정함
    // 하지만 현재는 잠깐에 에러가 생겨서 allwoedUrl 변수를 만들어서 해당 호스트만
    // Cors 설정함

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns(allowedOrigins)
                .withSockJS();
    }

    // 메시지 브로커의 Prefixes를 등록하는 부분
    // 클라이언트는 토픽을 구독할 시 /topic또는 /queue 경로로 요청해야함
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 외부 RabbitMQ STOMP 브로커 Relay
        // Broker Relay: RabbitMQ와 연동해 /topic/** 구독자에게 메시지 브로드캐스트
        // 접두사가 /topic이면 무조건 외부브로커에게 위임(Relay)되어
        // 구독(subscribe) 중인 클라인언트로 브로드 캐스트
//        registry.enableStompBrokerRelay("/topic", "/queue")
//                .setRelayHost("localhost")
//                .setRelayPort(61613)
//                .setClientLogin("guest")
//                .setClientPasscode("guest");
        // 클라이언트 → 서버 메시지 prefix
        // 클라이언트가 /app/** 으로 보낸 메시지는 @MessageMapping으로 라우팅
        // 이떄 라우팅 될때는 /app을 뗴고 /**으로 라우팅됨
        // 접두사가 /app이면 spring 내에 있는 @MessageMapping 메서드로 라우팅됨
        registry.enableSimpleBroker("/topic","/queue");
        registry.setApplicationDestinationPrefixes("/app");
    }


}

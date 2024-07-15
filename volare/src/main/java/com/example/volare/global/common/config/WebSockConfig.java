package com.example.volare.global.common.config;

import com.example.volare.global.common.auth.ChatErrorHandler;
import com.example.volare.global.common.auth.ChatPreHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSockConfig implements WebSocketMessageBrokerConfigurer {
    private final ChatPreHandler chatPreHandler;
    private final ChatErrorHandler chatErrorHandler;

    // 메시지 브로커의 Prefix를 등록하는 부분.
    // 클라이언트는 토픽을 구독할 시 /sub 경로로 요청해야 함.
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub");
        config.setApplicationDestinationPrefixes("/pub");
    }

    // stomp 접속 주소 url = ws://localhost:8080/websocket
    // 웹소켓 연결에 필요한 Endpoint를 지정함과 동시에 setAllowedOriginPatterns 부분을 애스터리스크(*)로 설정하여 모든 출처에 대한 Cors 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket").setAllowedOriginPatterns("*");
//        registry.setErrorHandler(chatErrorHandler);
    }

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(chatPreHandler);
//    }
}

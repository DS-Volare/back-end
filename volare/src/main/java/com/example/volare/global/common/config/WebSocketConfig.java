package com.example.volare.global.common.config;

import com.example.volare.global.common.webSocket.ChatErrorHandler;
import com.example.volare.global.common.webSocket.ChatPreHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final ChatPreHandler chatPreHandler;
    private final ChatErrorHandler chatErrorHandler;

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
        registry.addEndpoint("/websocket").setAllowedOrigins("*")
                .withSockJS(); // sock.js를 통하여 낮은 버전의 브라우저에서도 websocket이 동작할수 있게 설정
        registry.addEndpoint("/websocket").setAllowedOriginPatterns("*");
//        registry.setErrorHandler(chatErrorHandler);
    }

    // Flask는 토큰이 없음으로 보류
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(chatPreHandler);
//    }
}

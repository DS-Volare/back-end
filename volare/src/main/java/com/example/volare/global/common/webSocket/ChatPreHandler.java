package com.example.volare.global.common.webSocket;

import com.example.volare.global.common.auth.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatPreHandler implements ChannelInterceptor {

    // websocket을 통해 들어온 요청이 처리 되기전 실행됨
    private final  JwtService jwtService;

    // websocket을 통해 들어온 요청이 처리 되기전 실행됨
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // websocket 연결시 헤더의 jwt token 유효성 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {
            String authToken = String.valueOf(accessor.getNativeHeader("X-AUTH-TOKEN"));
            jwtService.checkValidationToken(authToken);

        }
        return message;
    }
}

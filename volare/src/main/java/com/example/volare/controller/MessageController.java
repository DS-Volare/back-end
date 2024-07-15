package com.example.volare.controller;

import com.example.volare.dto.MessageDTO;
import com.example.volare.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
public class MessageController {
    private final SimpMessageSendingOperations template;
    private final MessageService messageService;


    //메시지 송신 및 수신, /pub가 생략된 모습. 클라이언트 단에선 /pub/chatRoom/{chatRoomId}로 요청
    @MessageMapping("/chatRoom/{chatRoomId}")
    public void sendMessage(@DestinationVariable("chatRoomId") String chatRoomId,
                            @Valid @RequestBody MessageDTO.MessageRequestDto message) {
        // Log the received message
        System.out.println("Received message: " + message);
        template.convertAndSend("/sub/chatRoom/" + chatRoomId, messageService.saveMessage(chatRoomId, message));
    }

    // 채팅 내역 조회

}

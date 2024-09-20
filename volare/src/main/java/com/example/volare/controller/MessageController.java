package com.example.volare.controller;

import com.example.volare.dto.ChatRoomDTO;
import com.example.volare.dto.MessageDTO;
import com.example.volare.global.apiPayload.ApiResponse;
import com.example.volare.global.common.auth.model.AuthUser;
import com.example.volare.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
public class MessageController {
    private final SimpMessageSendingOperations template;
    private final MessageService messageService;


    //메시지 송신 및 수신, /pub가 생략된 모습. 클라이언트 단에선 /pub/chatRoom/{chatRoomId}로 요청
    @MessageMapping("/chats/{chatRoomId}")
    public void sendMessage(@DestinationVariable("chatRoomId") String chatRoomId,
                            @Valid @RequestBody MessageDTO.MessageRequestDto question) {

        // TODO: 병렬처리: sendGPTMessage 비동기 통신 중, saveMessage 저장
        // flask 연결- 비동기적으로 GPT 메시지 호출
        Mono<MessageDTO.MessageResponseDto> responseMono = messageService.sendGPTMessage(chatRoomId, question);

        MessageDTO.MessageResponseDto userQuestion = messageService.saveMessage(chatRoomId, question);
        template.convertAndSend("/sub/chats/" + chatRoomId, userQuestion);

        responseMono.subscribe(gptAnswer -> template.convertAndSend("/sub/chats/" + chatRoomId, gptAnswer));
    }

    // 채팅 내역 조회
    @GetMapping("/chats/{chatRoomId}")
    public ApiResponse<ChatRoomDTO.ChatRoomAllMessageResponseDto> getChatRoomMessages(@AuthenticationPrincipal AuthUser authUser,
                                                                                      @PathVariable("chatRoomId") String chatRoomId,
                                                                                      @RequestParam(required = false) String lastMessageId) {
        ChatRoomDTO.ChatRoomAllMessageResponseDto chatRoomMessages = messageService.getChatRoomMessages(authUser.getUser(), chatRoomId,lastMessageId);
        return ApiResponse.onSuccess(chatRoomMessages);
    }

}

package com.example.volare.service;

import com.example.volare.dto.MessageDTO;
import com.example.volare.global.apiPayload.code.status.ErrorStatus;
import com.example.volare.global.apiPayload.exception.handler.GeneralHandler;
import com.example.volare.model.ChatRoomEntity;
import com.example.volare.model.MessageEntity;
import com.example.volare.repository.ChatRoomRepository;
import com.example.volare.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 메시지 DB 저장
    @Transactional
    public MessageDTO.MessageResponseDto saveMessage(String chatRoomId, MessageDTO.MessageRequestDto message){

        // 채팅방 유효성 검사
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));

        MessageEntity.MessageType messageType = MessageEntity.MessageType.valueOf(message.getMessageType());

        MessageEntity saveMessage = MessageEntity.builder()
                .message(message.getMessage())
                .chatRoom(chatRoom)
                .messagetype(messageType)
                .build();
        messageRepository.save(saveMessage);

        // STOMP 프로토콜을 사용하여 해당 채팅방의 구독자들에게 전송
        return MessageDTO.fromEntity(saveMessage);
    }
}

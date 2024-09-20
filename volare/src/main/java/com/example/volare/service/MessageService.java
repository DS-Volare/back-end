package com.example.volare.service;

import com.example.volare.dto.ChatRoomDTO;
import com.example.volare.dto.MessageDTO;
import com.example.volare.global.apiPayload.code.status.ErrorStatus;
import com.example.volare.global.apiPayload.exception.handler.GeneralHandler;
import com.example.volare.model.ChatRoomEntity;
import com.example.volare.model.MessageEntity;
import com.example.volare.model.Novel;
import com.example.volare.model.User;
import com.example.volare.repository.ChatRoomRepository;
import com.example.volare.repository.MessageRepository;
import com.example.volare.repository.NovelRepository;
import com.example.volare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final NovelRepository novelRepository;

    // 메시지 DB 저장
    @Transactional
    public MessageDTO.MessageResponseDto saveMessage(String chatRoomId, MessageDTO.MessageRequestDto message){

        // 채팅방 유효성 검사
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));

        MessageEntity saveMessage = MessageEntity.builder()
                .message(message.getMessage())
                .chatRoom(chatRoom)
                .messagetype(Objects.equals(message.getMessageType(), MessageEntity.MessageType.QUESTION.name()) ? MessageEntity.MessageType.QUESTION : MessageEntity.MessageType.GPT)
                .build();
        MessageEntity chat = messageRepository.save(saveMessage);

        /* 메시지가 저장된 후, 채팅방이 속한 Novel의 수정 시간 갱신*/
        Novel novel = chatRoom.getScript().getNovel();
        novel.updateTimestamp(LocalDateTime.now());
        novelRepository.save(novel);

        // STOMP 프로토콜을 사용하여 해당 채팅방의 구독자들에게 전송
        return MessageDTO.fromEntity(chat);
    }

    public ChatRoomDTO.ChatRoomAllMessageResponseDto getChatRoomMessages(User user,String chatRoomId){
        // 채팅방 존재 검증
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));

        // 사용자 존재 검증, 채팅방 생성한 주인
        User chatRoomUser = userRepository.findById(user.getId()).orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));
        if(!chatRoom.getUser().getId().equals(chatRoomUser.getId())){
            throw new GeneralHandler(ErrorStatus._BAD_REQUEST);
        }
        List<MessageEntity> messageEntityList = messageRepository.searchByChatRoom(chatRoom);
        List<MessageDTO.MessageResponseDto> messageResponseDtos = messageEntityList.stream().map(MessageDTO::fromEntity).toList();
        return ChatRoomDTO.ChatRoomAllMessageResponseDto.builder().chatRoomId(chatRoomId).allMessages(messageResponseDtos).build();
    }
}

package com.example.volare.service;

import com.example.volare.dto.ChatRoomDTO;
import com.example.volare.dto.MessageDTO;
import com.example.volare.global.apiPayload.code.status.ErrorStatus;
import com.example.volare.global.apiPayload.exception.handler.GeneralHandler;
import com.example.volare.model.ChatRoomEntity;
import com.example.volare.model.MessageEntity;
import com.example.volare.model.User;
import com.example.volare.repository.ChatRoomRepository;
import com.example.volare.repository.MessageRepository;
import com.example.volare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    private final WebClientService webClientService;

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    // 메시지 DB 저장
    @Transactional
    public void saveMessage(String chatRoomId, MessageDTO.MessageRequestDto message){
        log.info("Finding chat room - save");
        // 채팅방 유효성 검사
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));
        MessageEntity saveMessage = MessageEntity.builder()
                .message(message.getMessage())
                .chatRoom(chatRoom)
                .messagetype(Objects.equals(message.getMessageType(), MessageEntity.MessageType.QUESTION.name()) ? MessageEntity.MessageType.QUESTION : MessageEntity.MessageType.GPT)
                .build();
        messageRepository.save(saveMessage);

    }

    // GPT 메세지 호출
    @Transactional
    public Mono<MessageDTO.MessageResponseDto> sendGPTMessage(String chatRoomId, MessageDTO.MessageRequestDto message) {
        // 채팅방 유효성 검사 및 WebClient 비동기 호출
        return Mono.fromCallable(() -> {
            log.info("Finding chat room with ID");
            return chatRoomRepository.findById(chatRoomId)
                    .orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));
        })        .flatMap(chatRoom -> {

            log.info("Chat room found");

                    // GPT 요청 DTO 생성
                    MessageDTO.MessageGPTRequestDto messageGPTRequestDto = MessageDTO.MessageGPTRequestDto
                            .builder()
                            .message(message.getMessage())
                            .context(chatRoomRepository.findStoryTextByChatRoomId(chatRoomId))
                            .build();

            log.info("Calling GPT with request");

                    // WebClient 비동기 호출
                    return webClientService.responseGPT(messageGPTRequestDto)
                            .flatMap(responseGPT -> {
                                log.info("Received GPT response");
                                // 메시지 엔티티 생성
                                MessageEntity messageEntity = MessageDTO.fromDto(responseGPT, chatRoom, MessageEntity.MessageType.GPT);

                                /// 메시지 저장 후 저장된 메시지 엔티티 반환
                                return Mono.fromCallable(() -> messageRepository.save(messageEntity))
                                        .map(MessageDTO::fromEntity); // 저장된 메시지 엔티티로부터 DTO 반환
                            });
                });
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

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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    private final WebClientService webClientService;

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper; // ObjectMapper 주입

    // 메시지 DB 저장
    @Transactional
    public MessageDTO.MessageResponseDto saveMessage(String chatRoomId, MessageDTO.MessageRequestDto message) {
        // 채팅방 유효성 검사
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));

        // 메시지 저장
        MessageEntity savedMessage = MessageEntity.builder()
                .message(message.getMessage())
                .chatRoomId(chatRoom.getId())
                .messagetype(Objects.equals(message.getMessageType(), "QUESTION") ?
                        MessageEntity.MessageType.QUESTION : MessageEntity.MessageType.GPT)
                .build();
        MessageEntity chat = messageRepository.save(savedMessage);

        // Kafka로 메시지 전송
        sendMessageToKafka(chat);
        return MessageDTO.fromEntity(chat);
    }

    private void sendMessageToKafka(MessageEntity messageEntity) {
        String topic = "GPT_Request_Topic";
        try {
            // 객체를 JSON 문자열로 변환
            String message = objectMapper.writeValueAsString(
                    new MessageDTO.MessageKafkaRequestDto(messageEntity)
            );

            // 변환된 JSON 문자열을 Kafka로 전송
            kafkaTemplate.send(topic, message); // KafkaTemplate<String, String>에 맞게 처리
            log.info("Sent JSON message to Kafka: {}", message);
        } catch (JsonProcessingException e) {
            log.error("Error converting message to JSON: {}", e.getMessage());
        }
    }


    // GPT 메세지 호출
    @Transactional
    public Mono<MessageDTO.MessageResponseDto> sendGPTMessage(String chatRoomId, String message) {
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
                            .message(message)
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

    public ChatRoomDTO.ChatRoomAllMessageResponseDto getChatRoomMessages(User user,String chatRoomId, String lastMessageId){
        // 채팅방 존재 검증
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));

        // 사용자 존재 검증, 채팅방 생성한 주인
        User chatRoomUser = userRepository.findById(user.getId()).orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));
        if(!chatRoom.getUser().getId().equals(chatRoomUser.getId())){
            throw new GeneralHandler(ErrorStatus._BAD_REQUEST);
        }

        /* 9개의 메시지를 가져와서 8개만 사용할 것*/
        PageRequest pageRequest = PageRequest.of(0, 9, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<MessageEntity> messageEntityList;

        if (lastMessageId == null) {
            // 처음 요청
            messageEntityList = messageRepository.findByChatRoomId(chatRoomId, pageRequest);
        } else {
            // 스크롤 요청: lastMessageId 이전 메시지
            messageEntityList = messageRepository.findByChatRoomIdAndIdLessThan(chatRoomId, lastMessageId, pageRequest);
        }

        // 메시지 개수로 다음 메시지 여부 확인
        boolean hasNext = messageEntityList.size() == 9;

        // 마지막 1개는 제외하고 전달
        if (hasNext) {
            messageEntityList = messageEntityList.subList(0, 8);
        }

        // 최신순으로 조회된 메시지 리스트를 오래된 순으로 뒤집기
        Collections.reverse(messageEntityList);

        List<MessageDTO.MessageResponseDto> messageResponseDtos = messageEntityList.stream().map(MessageDTO::fromEntity).collect(Collectors.toList());
        return ChatRoomDTO.ChatRoomAllMessageResponseDto.builder()
                .chatRoomId(chatRoomId)
                .allMessages(messageResponseDtos)
                .hasNext(hasNext)
                .build();
    }
}

package com.example.volare.service;

import com.example.volare.dto.MessageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final MessageService messageService;
    private final ObjectMapper objectMapper; // ObjectMapper 주입
    private final SimpMessageSendingOperations template;

    // flask 연결- 비동기적으로 GPT 메시지 호출
    @KafkaListener(topics = "GPT_Request_Topic", groupId = "your_group_id")
    public void listen(String message) {
        log.info("Received message from Kafka: {}", message);
        try {
            // 메시지를 DTO로 변환
            MessageDTO.MessageKafkaRequestDto requestDto = objectMapper.readValue(message, MessageDTO.MessageKafkaRequestDto.class);
            String chatRoomId = requestDto.getChatRoomId();
            String question = requestDto.getMessage();

            // 비동기적으로 GPT 메시지 호출
            Mono<MessageDTO.MessageResponseDto> responseMono = messageService.sendGPTMessage(chatRoomId, question);

            // 처리된 응답 처리
            responseMono.subscribe(gptAnswer -> {
                log.info("GPT로부터 받은 응답: {}", gptAnswer);
                // 응답을 WebSocket으로 전송
                template.convertAndSend("/sub/chats/" + chatRoomId, gptAnswer);
            });

        } catch (JsonProcessingException e) {
            log.error("Error processing JSON: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage());
        }
    }


}

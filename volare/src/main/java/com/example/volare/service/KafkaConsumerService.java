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
    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations template;

    @KafkaListener(topics = "GPT_Request_Topic", groupId = "Flask-Chat")
    public void listen(String message) {
        log.info("Received message from Kafka: {}", message);
        try {
            MessageDTO.MessageKafkaRequestDto requestDto = objectMapper.readValue(message, MessageDTO.MessageKafkaRequestDto.class);
            String chatRoomId = requestDto.getChatRoomId();
            String question = requestDto.getMessage();

            // GPT 메시지 호출
            Mono<MessageDTO.MessageResponseDto> responseMono = messageService.sendGPTMessage(chatRoomId, question);
            responseMono.subscribe(gptAnswer -> {
                log.info("GPT response: {}", gptAnswer);
                template.convertAndSend("/sub/chats/" + chatRoomId, gptAnswer);
            });
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON: {}", e.getMessage());
        }
    }
}

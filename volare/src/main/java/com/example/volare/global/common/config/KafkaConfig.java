package com.example.volare.global.common.config;

import com.example.volare.dto.MessageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.util.backoff.FixedBackOff;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@Slf4j
@RequiredArgsConstructor
public class KafkaConfig {

    private final SimpMessageSendingOperations template;
    private final ObjectMapper objectMapper;

    // ConsumerFactory 설정
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "your_group_id");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    // ProducerFactory 설정
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    // KafkaTemplate 빈 등록
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // 실패 메시지를 WebSocket으로 전송하는 메서드
    private void sendErrorMessageToChatRoom(String chatRoomId, String errorMessage) {
        try {
            // WebSocket을 통해 실패 메시지 전송
            template.convertAndSend("/sub/chats/" + chatRoomId, errorMessage);
        } catch (Exception e) {
            log.error("Error sending error message to chat room: {}", e.getMessage());
        }
    }

    // 에러 핸들러 설정
    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, String> kafkaTemplate) {
        FixedBackOff backOff = new FixedBackOff(2000L, 3L); // 3번 재시도, 2초 간격
        return new DefaultErrorHandler((record, exception) -> {
            if (record instanceof ConsumerRecord) {
                ConsumerRecord<String, String> consumerRecord = (ConsumerRecord<String, String>) record;
                log.error("Error processing record: {}", consumerRecord.value(), exception);

                // 실패한 메시지를 DLT로 전송
//                kafkaTemplate.send("GPT_Request_Topic.DLT", String.valueOf(consumerRecord.value()));

                // 실패 메시지를 클라이언트로 전송
                String failedMessage = "서버 오류로 메시지를 처리할 수 없습니다. 다시 시도해주세요.";
                String chatRoomId = extractChatRoomIdFromRecord(consumerRecord, objectMapper); // record에서 chatRoomId 추출

                // WebSocket을 통해 실패 메시지 전송
                sendErrorMessageToChatRoom(chatRoomId, failedMessage); // sendErrorMessageToChatRoom 호출
            }
        }, backOff);
    }

    private String extractChatRoomIdFromRecord(ConsumerRecord<String, String> record, ObjectMapper objectMapper) {
        // Kafka 메시지에서 chatRoomId를 추출하는 로직
        try {
            MessageDTO.MessageKafkaRequestDto requestDto = objectMapper.readValue(record.value(), MessageDTO.MessageKafkaRequestDto.class);
            return requestDto.getChatRoomId();
        } catch (JsonProcessingException e) {
            log.error("Error extracting chatRoomId from message", e);
            return "unknown"; // 기본값 처리
        }
    }

    // KafkaListener 컨테이너 팩토리
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory,
            KafkaTemplate<String, String> kafkaTemplate) {

        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler(kafkaTemplate)); // 에러 핸들러 등록
        return factory;
    }
}

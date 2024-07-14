package com.example.volare.dto;

import com.example.volare.model.MessageEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MessageDTO {

// REQUEST
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
        public static class MessageRequestDto{
        private String message;
        private String messageType;
    }

//RESPONSE
    @Builder
    @Getter
    @AllArgsConstructor
    public static class MessageResponseDto {
        private String messageId;
        private String messageType;
        private String message;
        private String createdAt;
}

//CONVERTER
    public static MessageResponseDto fromEntity(MessageEntity message){
        return MessageResponseDto.builder()
                .messageId(message.getId())
                .messageType(message.getMessagetype().name())
                .message(message.getMessage())
                .createdAt(message.getCreatedAt().toString())
                .build();
    }

}

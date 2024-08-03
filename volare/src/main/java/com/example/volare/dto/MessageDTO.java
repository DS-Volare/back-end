package com.example.volare.dto;

import com.example.volare.global.common.DateUtil;
import com.example.volare.model.ChatRoomEntity;
import com.example.volare.model.MessageEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class MessageDTO {

// REQUEST
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
        public static class MessageRequestDto{
        @NotBlank(message = "메시지는 필수입니다.")
        private String message;

        @NotBlank(message = "메시지는 필수입니다.")
        private String messageType;
    }

    @Data // 직렬화,역직렬화시 toString 필요
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageGPTRequestDto{
        @NotBlank(message = "메시지는 필수입니다.")
        private String message;
        private String context;

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

    @Getter
    public static class  MessageGPTResponseDto{
        private String answer;
    }

//CONVERTER
    public static MessageResponseDto fromEntity(MessageEntity message){
        return MessageResponseDto.builder()
                .messageId(message.getId())
                .messageType(message.getMessagetype().name())
                .message(message.getMessage())
                .createdAt(DateUtil.convert(message.getCreatedAt()))
                .build();
    }

    public static MessageEntity fromDto(MessageGPTResponseDto message, ChatRoomEntity chatRoom, MessageEntity.MessageType messageType){
        return MessageEntity.builder()
                .message(message.getAnswer())
                .chatRoom(chatRoom)
                .messagetype(messageType)
                .build();
    }
}

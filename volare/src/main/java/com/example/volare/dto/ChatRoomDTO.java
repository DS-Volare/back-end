package com.example.volare.dto;

import com.example.volare.model.ChatRoomEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ChatRoomDTO {

// REQUEST


//RESPONSE

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRoomResponseDto{

        private String userId;
        private String chatRoomId;


    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRoomAllMessageResponseDto{

        private String chatRoomId;
        List<MessageDTO.MessageResponseDto> allMessages;

    }



//CONVERTER
    public static ChatRoomDTO.ChatRoomResponseDto fromDTO(ChatRoomEntity chatRoom){
        return ChatRoomResponseDto.builder()
                .userId(chatRoom.getUser().getId())
                .chatRoomId(chatRoom.getId()).build();
    }
}

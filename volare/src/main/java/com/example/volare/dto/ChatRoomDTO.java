package com.example.volare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatRoomDTO {

// REQUEST


//RESPONSE

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRoomResponseDto{
        private String chatRoomId;

    }

//CONVERTER
    public static ChatRoomDTO.ChatRoomResponseDto fromDTO(String chatRoomId){
        return ChatRoomResponseDto.builder().chatRoomId(chatRoomId).build();
    }
}

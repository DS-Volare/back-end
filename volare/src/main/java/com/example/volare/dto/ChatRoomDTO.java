package com.example.volare.dto;

import com.example.volare.model.ChatRoomEntity;
import com.example.volare.model.MessageEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

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
        private Boolean hasPrevious;
        private Boolean hasNext;
    }



//CONVERTER
    public static ChatRoomDTO.ChatRoomResponseDto fromDTO(ChatRoomEntity chatRoom){
        return ChatRoomResponseDto.builder()
                .userId(chatRoom.getUser().getId())
                .chatRoomId(chatRoom.getId()).build();
    }

    public static ChatRoomAllMessageResponseDto convert(
            String chatRoomId, List<MessageDTO.MessageResponseDto> messageResponseDtos, Page<MessageEntity> page
    )
    {
        return ChatRoomAllMessageResponseDto.builder()
                .chatRoomId(chatRoomId)
                .allMessages(messageResponseDtos)
                .hasPrevious(page.hasPrevious())
                .hasNext(page.hasNext())
                .build();
    }
}

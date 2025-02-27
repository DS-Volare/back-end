package com.example.volare.dto;

import com.example.volare.model.ChatRoomEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConvertDetailDTO {
    private NovelDTO.NovelDetailResponseDTO novel;
    private ConvertWrapper<ScriptDTO.ScriptDetailResponseDTO>  script;
    private String chatRoomId;
    private ConvertWrapper<StoryboardDTO.Response> storyBoard;

    public static ConvertDetailDTO fromDTO( NovelDTO.NovelDetailResponseDTO novel,
                                            ConvertWrapper<ScriptDTO.ScriptDetailResponseDTO>  script,
                                            ChatRoomEntity chatRoom,
                                            ConvertWrapper<StoryboardDTO.Response> storyBoard){
        return ConvertDetailDTO.builder()
                .novel(novel)
                .script(script)
                .chatRoomId(chatRoom != null ? chatRoom.getId() : null)
                .storyBoard(storyBoard).build();
    }
}

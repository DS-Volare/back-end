package com.example.volare.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConvertDetailDTO {
    private NovelDTO.NovelDetailResponseDTO novel;
    private ConvertWrapper<ScriptDTO.ScriptDetailResponseDTO>  script;
    private ConvertWrapper<StoryboardDTO.Response> storyBoard;

    public static ConvertDetailDTO fromDTO( NovelDTO.NovelDetailResponseDTO novel,
                                            ConvertWrapper<ScriptDTO.ScriptDetailResponseDTO>  script,
                                            ConvertWrapper<StoryboardDTO.Response> storyBoard){
        return ConvertDetailDTO.builder().novel(novel).script(script).storyBoard(storyBoard).build();
    }
}

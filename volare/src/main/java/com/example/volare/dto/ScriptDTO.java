package com.example.volare.dto;

import com.example.volare.model.Script;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

public class ScriptDTO {

    @Getter
    public static class ScriptRequestDTO{
        private List<String> candidates;
        private String text;
    }


    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class NovelToStoryScriptResponseDTO {
        private Script script;
        private String script_str;


        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        public static class Script {

            private  List<Scene> scene;

            @Builder
            @AllArgsConstructor
            @NoArgsConstructor
            @Getter
            public static class Scene  {
                private List<Content> content;
                private String location;
                private int scene_num;
                private String time;

                @Builder
                @AllArgsConstructor
                @NoArgsConstructor
                @Getter
                public static class Content {
                    private String action;
                    private String character;
                    private String dialog;
                    private String type;
                }
            }
        }
    }

//CONVERTER
    public static NovelToStoryScriptResponseDTO EntityToDTO(Script scriptEntity){
        return ScriptDTO.NovelToStoryScriptResponseDTO.builder()
                .script(ScriptDTO.NovelToStoryScriptResponseDTO.Script.builder()
                        .scene(scriptEntity.getScriptScenes().stream()
                                .map(scene -> ScriptDTO.NovelToStoryScriptResponseDTO.Script.Scene.builder()
                                        .location(scene.getLocates())
                                        .scene_num(scene.getSceneNum())
                                        .time(scene.getTime())
                                        .content(scene.getContents().stream()
                                                .map(content -> ScriptDTO.NovelToStoryScriptResponseDTO.Script.Scene.Content.builder()
                                                        .action(content.getAction())
                                                        .character(content.getCharacter())
                                                        .dialog(content.getDialog())
                                                        .build()
                                                )
                                                .collect(Collectors.toList())
                                        )
                                        .build()
                                )
                                .collect(Collectors.toList())
                        )
                        .build()
                )
                .script_str(scriptEntity.getScriptFile())
                .build();
    }

}

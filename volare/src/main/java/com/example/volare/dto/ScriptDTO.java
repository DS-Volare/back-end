package com.example.volare.dto;

import com.example.volare.model.Script;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class ScriptDTO {

    @Getter
    public static class ScriptRequestDTO{
        private List<String> candidates;
        private String text;
    }


    @Builder
    @AllArgsConstructor
    @Getter
    public static class NovelToStoryScriptResponseDTO {
        private Script script;
        private String script_str;


        @Builder
        @AllArgsConstructor
        @Getter
        public static class Script {

            //TODO: 대본 하나당 단일 값인지 확인
            private  List<Scene> scene;

            @Builder
            @AllArgsConstructor
            @Getter
            public static class Scene  {
                private List<Content> content;
                private String location;
                private int scene_num;
                private String time;

                @Builder
                @AllArgsConstructor
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

        
    }

}

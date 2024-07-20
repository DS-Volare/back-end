package com.example.volare.dto;

import lombok.Getter;

import java.util.List;

public class ScriptDTO {

    @Getter
    public static class ScriptRequestDTO{
        private String text;
    }


    @Getter
    public static class NovelToStoryScriptResponseDTO {
        private Script script;
        private String script_str;


        @Getter
        public static class Script {

            //TODO: 대본 하나당 단일 값인지 확인
            private Scene scene;

            @Getter
            public static class Scene  {
                private List<Content> content;
                private String location;
                private int scene_num;
                private String time;

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

}

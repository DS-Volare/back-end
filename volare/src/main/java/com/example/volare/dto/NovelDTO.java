package com.example.volare.dto;

import lombok.Getter;

import java.util.List;

public class NovelDTO {

    @Getter
    public static class NovelRequestDTO{
        private String exchangeNovel;
    }


    @Getter
    public static class NovelToStoryScriptResponseDTO {
        private Script script;
        private String script_str;

        @Getter
        public static class Script {
            private List<Scene > scene;

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

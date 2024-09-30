package com.example.volare.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class StoryboardDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @JsonProperty("scriptId")
        private Long scriptId;

        @JsonProperty("script")
        private Script script;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Script {
            @JsonProperty("scene")
            private List<Scene> scene;

            @Getter
            @NoArgsConstructor
            @AllArgsConstructor
            @Builder
            public static class Scene {
                @JsonProperty("scene_num")
                private int scene_num;

                @JsonProperty("location")
                private String location;

                @JsonProperty("content")
                private List<Content> content;

                @Getter
                @NoArgsConstructor
                @AllArgsConstructor
                @Builder
                public static class Content {
                    @JsonProperty("action")
                    private String action;

                    @JsonProperty("character")
                    private String character;

                    @JsonProperty("dialog")
                    private String dialog;

                    @JsonProperty("type")
                    private String type;
                }
            }
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        @JsonProperty("scene")
        private List<Scene> scene;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Scene {
            @JsonProperty("scene_num")
            private int scene_num;

            @JsonProperty("location")
            private String location;

            @JsonProperty("time")
            private String time;

            @JsonProperty("content")
            private List<Cut> cuts;

            @Getter
            @NoArgsConstructor
            @AllArgsConstructor
            @Builder
            public static class Cut {
                @JsonProperty("cut_image")
                private String cut_image;

                @JsonProperty("cut_num")
                private int cut_num;

                @JsonProperty("text")
                private String text;
            }
        }
    }
}
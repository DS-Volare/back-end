package com.example.volare.dto;

import com.example.volare.model.StoryBoard;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Long storyBoardId;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Scene {
            @JsonProperty("scene_num")
            private String scene_num;

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


//CONVERT
    // 엔티티 -> Response DTO 변환
    public static Response storyBoardConvertToDto(StoryBoard storyBoard) {
        // StoryBoardCut 리스트를 Scene 내의 cuts로 변환
        List<Response.Scene.Cut> cuts = storyBoard.getCuts().stream()
                .map(cut -> Response.Scene.Cut.builder()
                        .cut_image(cut.getCutImage()) // 엔티티의 cutImage 사용
                        .cut_num(cut.getCutNum())     // 엔티티의 cutNum 사용
                        .build())
                .collect(Collectors.toList());

        // StoryBoard를 Scene으로 변환
        Response.Scene scene = Response.Scene.builder()
                .scene_num(storyBoard.getSceneNum()) // sceneNum 사용
                .location(storyBoard.getLocate())                     // location 사용
                .time(storyBoard.getTime())                           // time 사용
                .cuts(cuts)                                           // 변환한 cuts 사용
                .build();

        // 최종적으로 Response에 Scene을 포함하여 반환
        return Response.builder()
                .scene(Collections.singletonList(scene)) // 단일 Scene을 포함
                .storyBoardId(storyBoard.getId())
                .build();
    }
}
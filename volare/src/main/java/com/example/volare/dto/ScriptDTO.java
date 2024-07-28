package com.example.volare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScriptDTO {
    private List<SceneDTO> scene;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SceneDTO {
        private int scene_num;
        private String location;
        private String time;
        private List<ContentDTO> content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentDTO {
        private String character;
        private String action;
        private String dialog;
        private String type;
    }
}
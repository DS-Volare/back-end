package com.example.volare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequestDTO {
    private int script_id;
    private List<UpdateDetailDTO> u_list;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateDetailDTO {
        private int sceneNumber;
        private int contentIndex;
        private String type;
        private String character;
        private String action;
        private String dialog;
    }
}
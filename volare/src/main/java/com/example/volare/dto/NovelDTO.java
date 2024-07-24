package com.example.volare.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NovelDTO {

// REQUEST
    @Getter
    public static class NovelRequestDTO {
    private String title;
    private String novel;

}


//RESPONSE
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NovelResponseDTO {
        private String novelId;
    }


//CONVERTER
}

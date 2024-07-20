package com.example.volare.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class NovelDTO {

// REQUEST
    @Getter
    public static class NovelRequestDTO {
    private String title;
    private String novel;

}


//RESPONSE
    @AllArgsConstructor
    public static class NovelResponseDTO {
        private String novelId;
    }


//CONVERTER
}

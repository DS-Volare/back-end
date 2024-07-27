package com.example.volare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
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


    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NovelCovertListDTO {
        private String title;
        private String image;

        // TODO: 소설 저장 시간 X, 최종 수정 시간 O
        /**
         * case 1) 채팅 내역 갱신 -> 최종 수정 시간 가장 마지막 채팅 시간 반영
         * case 2) 마지막 단계 - 스토리보드 생성 시간 = 수정 시간
         */
        private String updatedAt;
    }

//CONVERTER
}

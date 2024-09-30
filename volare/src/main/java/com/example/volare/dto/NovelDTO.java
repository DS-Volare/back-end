package com.example.volare.dto;

import com.example.volare.model.Novel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

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
    public static class UserConvertDTO {
        private long totalItems;  // 전체 항목 수
        private int totalPages;  // 전체 페이지 수
        private boolean hasPrevious; // 이전 페이지 존재 여부
        private boolean hasNext; // 다음 페이지 존재 여부

        private List<NovelConvertListDTO> userConvertListDTO;

    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NovelConvertListDTO {
        private String id;
        private String title;
        private String image;

        // 소설 저장 시간 X, 최종 수정 시간 O
        /**
         * case 1) 채팅 내역 갱신 -> 최종 수정 시간 가장 마지막 채팅 시간 반영 -> Novel updateAt컬럼으로 관리
         * case 2) 마지막 단계 - 스토리보드 생성 시간 = Novel updateAt컬럼으로 관리
         */
        private String updatedAt;
    }

//CONVERTER
public static UserConvertDTO conversionConvert(Page<Novel> novelList, List<NovelConvertListDTO> listDTO) {
    return UserConvertDTO.builder()
            .totalItems((int) novelList.getTotalElements()) // 전체 항목 수
            .totalPages(novelList.getTotalPages()) // 전체 페이지 수
            .hasPrevious(novelList.hasPrevious()) // 이전 페이지 여부
            .hasNext(novelList.hasNext()) // 다음 페이지 여부
            .userConvertListDTO(listDTO) // 변환된 DTO 리스트
            .build();
}
}

package com.example.volare.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class EmailDTO {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmailRequestDTO{
        private String  novelId;
        private List<MultipartFile> pdfFiles; // 첨부 파일 리스트 (최대 3개)
    }


}

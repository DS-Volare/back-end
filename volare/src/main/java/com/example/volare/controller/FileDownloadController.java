package com.example.volare.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
public class FileDownloadController {

    @GetMapping("/script/downloads/{sb_id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String sb_id) {
        try {
            // sb_id에 따라 파일 이름 생성
            String filename = "file_" + sb_id + ".txt"; // 예시로 파일 이름을 구성
            Resource resource = new ClassPathResource("uploads/" + filename);  // resources/files 폴더에 있는 파일

            // 파일이 존재하는지 확인
            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

            // ResponseEntity를 사용하여 파일 다운로드 응답 반환
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(resource.contentLength())
                    .body(new InputStreamResource(resource.getInputStream()));
        } catch (IOException e) {
            // IO 예외 발생 시 내부 서버 오류 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
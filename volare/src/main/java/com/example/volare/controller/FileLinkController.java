package com.example.volare.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileLinkController {

    @Value("${server.port}")
    private int serverPort;

    @GetMapping("/script/link/{sb_id}")
    public String getFileDownloadLink(@PathVariable String sb_id) {
        // 파일 다운로드 URL 생성
        String downloadUrl = "http://localhost:" + serverPort + "/script/download/" + sb_id;
        return "Download your file from: " + downloadUrl;
    }
}
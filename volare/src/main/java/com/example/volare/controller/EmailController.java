package com.example.volare.controller;

import com.example.volare.dto.EmailDTO;
import com.example.volare.global.apiPayload.ApiResponse;
import com.example.volare.global.common.auth.model.AuthUser;
import com.example.volare.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.volare.global.apiPayload.code.status.ErrorStatus.EMAIL_NOT_SEND;

@RestController
@RequestMapping("api/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ApiResponse<?> sendEmail(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestPart("novelId") String novelId,
            @RequestPart(value = "pdfFiles", required = false) List<MultipartFile> pdfFiles) {

        if (pdfFiles == null || pdfFiles.isEmpty()) {
            return ApiResponse.onFailure("EMAIL_NOT_SEND", "첨부 파일이 없습니다.", novelId);
        }

        if (pdfFiles.size() > 3) {
            return ApiResponse.onFailure("EMAIL_NOT_SEND", "첨부 파일은 최대 3개까지만 가능합니다.", novelId);
        }

        List<String> fileNames = new ArrayList<>();
        List<byte[]> fileDataList = new ArrayList<>();

        try {
            for (MultipartFile file : pdfFiles) {
                if (file.isEmpty()) {
                    return ApiResponse.onFailure("EMAIL_NOT_SEND", "첨부 파일 중 일부가 비어 있습니다.", novelId);
                }
                fileNames.add(file.getOriginalFilename());
                fileDataList.add(file.getBytes());
            }

            // 이메일 서비스 호출
            emailService.sendMailWithPdf(novelId, authUser.getUser(), fileNames, fileDataList);
            return ApiResponse.onSuccess("이메일 전송 완료");

        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            return ApiResponse.onFailure("EMAIL_NOT_SEND", "이메일 전송 중 오류 발생: " + e.getMessage(), novelId);
        } catch (IllegalArgumentException e) {
            return ApiResponse.onFailure("EMAIL_NOT_SEND", e.getMessage(), novelId);
        }
    }
}

package com.example.volare.service;

import com.example.volare.global.apiPayload.code.status.ErrorStatus;
import com.example.volare.global.apiPayload.exception.handler.GeneralHandler;
import com.example.volare.model.Novel;
import com.example.volare.model.User;
import com.example.volare.repository.NovelRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final NovelRepository novelRepository;

    @Transactional(readOnly = true)
    public void sendMailWithPdf(String  novelId, User user,List<String> fileNames, List<byte[]> fileDataList) throws MessagingException {
        // 1. DB에서 소설 조회
        Novel novel = novelRepository.findById(novelId).orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));

        // 2. 메일 전송
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("2024servervolare@gmail.com");
        helper.setTo(user.getEmail()); // DB에서 조회한 유저 이메일
        helper.setSubject("[Volare 변환 내역 파일 전송] " + novel.getTitle());

        // HTML 내용 설정
        String htmlContent = "<h3 style='color:blue;'>대본 및 스토리보드 변환 신청:</h3>" +
                novel.getTitle()+"<br> <p>파일을 전송드립니다. 서비스를 이용해주셔서 감사합니다</p>";
        helper.setText(htmlContent, true);

        // 첨부 파일 추가
        for (int i = 0; i < fileDataList.size(); i++) {
            helper.addAttachment(fileNames.get(i), new ByteArrayResource(fileDataList.get(i)));
        }

        mailSender.send(message);
    }

}

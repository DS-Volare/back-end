package com.example.volare.global.common.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final ObjectMapper objectMapper;
    private final HttpSession httpSession;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 세션에서 토큰 가져오기
        String accessToken = (String) httpSession.getAttribute("accessToken");
        String refreshToken = (String) httpSession.getAttribute("refreshToken");

        // 응답 설정
        response.setCharacterEncoding("utf-8");
        response.setHeader("X-AUTH-TOKEN", accessToken);
        response.setHeader("refresh-token", refreshToken);
        response.setContentType("application/json");

        // 응답 본문 설정 (필요에 따라 수정 가능)
        // String body = objectMapper.writeValueAsString(new TokenDTO(accessToken, refreshToken));
        // response.getWriter().write(body);
    }
}
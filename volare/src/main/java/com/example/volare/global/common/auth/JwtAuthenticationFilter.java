package com.example.volare.global.common.auth;

import com.example.volare.global.apiPayload.code.status.ErrorStatus;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    // 필터 적용 x
    private static final List<String> EXCLUDE_URLS = Arrays.asList(
            "/users/reissue-token"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("X-AUTH-TOKEN");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 요청된 경로가 필터링 제외 경로에 포함되어 있는지 확인
        String path = request.getRequestURI();
        if (EXCLUDE_URLS.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if(token != null && jwtService.checkValidationToken(token)) {
                Authentication authentication = jwtService.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        }  catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            String jsonResponse = String.format("{\"error\": {\"code\": \"%s\", \"message\": \"%s\"}}",
                    ErrorStatus.EXPIRED_ACCESS_TOKEN.getCode(), ErrorStatus.EXPIRED_ACCESS_TOKEN.getMessage());
            response.getWriter().write(jsonResponse);
        } catch (UsernameNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"유저를 찾을 수 없습니다.\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            String jsonResponse = String.format("{\"error\": {\"code\": \"%s\", \"message\": \"%s\"}}",
                    ErrorStatus._INVALID_ACCESS_TOKEN.getCode(), ErrorStatus._INVALID_ACCESS_TOKEN.getMessage());
            response.getWriter().write(jsonResponse);
        }
    }
}
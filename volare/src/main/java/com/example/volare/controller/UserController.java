package com.example.volare.controller;

import com.example.volare.global.apiPayload.ApiResponse;
import com.example.volare.global.common.auth.JwtService;
import com.example.volare.global.common.auth.model.TokenDTO;
import com.example.volare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final JwtService jwtService;
    private final UserService userService;

    // 로그아웃
    @PostMapping("/sign-out")
    public ApiResponse<?> signOut(@RequestHeader("X-AUTH-TOKEN") String accessToken,
                                  @RequestHeader("refresh-Token") String refreshToken) {
        userService.signOut(accessToken, refreshToken);
        return ApiResponse.onSuccess("");
    }

    // 토큰 재발급
    @PostMapping("/reissue-token")
    public ResponseEntity reissueToken(@RequestHeader("X-AUTH-TOKEN") String accessToken,
                                       @RequestHeader("refresh-Token") String refreshToken) {
        TokenDTO tokens = jwtService.reissueToken(accessToken, refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-AUTH-TOKEN", tokens.getAccessToken());
        headers.add("refresh-token", tokens.getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(headers)
                .build();
    }
}

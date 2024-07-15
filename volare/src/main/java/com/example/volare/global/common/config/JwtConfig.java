package com.example.volare.global.common.config;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class JwtConfig {
    public static SecretKey RANDOM_SECRET_KEY = Keys.hmacShaKeyFor(JwtConfig.SECRET_KEY_VALUE.getBytes(StandardCharsets.UTF_8));
    public final static String SECRET_KEY_VALUE = "IssuedByGustoServerWroteByMinnie";

    // 현재 ACCESS_TOKEN 유효기간을 길게 잡아놓은 상태, 차후 서비스 출시 시엔 2일로 변경 예정
    // 1000L * 60 * 60 * 24 : 1일
    public static long ACCESS_TOKEN_VALID_TIME = 1000L * 60 * 60 * 24 * 7; //7일
    // 현재 재로그인 및 refresh token을 통한 토큰 재발급에 대한 처리가 되어 있지 않아 부득이하게 1시간만 저장
    public static long  REFRESH_TOKEN_VALID_TIME = 1000L * 60 * 60 * 24 * 1;
}

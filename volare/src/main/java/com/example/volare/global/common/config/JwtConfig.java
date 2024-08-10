package com.example.volare.global.common.config;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class JwtConfig {
    public static SecretKey RANDOM_SECRET_KEY = Keys.hmacShaKeyFor(JwtConfig.SECRET_KEY_VALUE.getBytes(StandardCharsets.UTF_8));
    public final static String SECRET_KEY_VALUE = "IssuedByVolareServerWroteByHyeSoo";

    public static long ACCESS_TOKEN_VALID_TIME = 1000L * 60 * 60 * 24 * 2 ; // 2일

    public static long  REFRESH_TOKEN_VALID_TIME = 1000L * 60 * 60 * 24 * 7; // 7일
}

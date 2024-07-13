package com.example.volare.global.common.auth;

import com.example.volare.global.common.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Service
public class JwtService implements InitializingBean {
    private static final String UUID = "uuid";
    private static SecretKey secretKey;


    @Override
    public void afterPropertiesSet() throws Exception {
        // jwt 시크릿 키 설정
        secretKey = JwtConfig.RANDOM_SECRET_KEY;
    }

    public TokenDTO createToken(String userUUID) {
        Date now = new Date();

        String accessToken = Jwts.builder()
                .expiration(new Date(now.getTime() + JwtConfig.ACCESS_TOKEN_VALID_TIME))
                .subject("access-token")
                .claim(UUID, userUUID)
                .issuedAt(now)
                .signWith(secretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .expiration(new Date(now.getTime() + JwtConfig.REFRESH_TOKEN_VALID_TIME))
                .subject("refresh-token")
                .claim(UUID, userUUID)
                .issuedAt(now)
                .signWith(secretKey)
                .compact();

        TokenDTO tokens = new TokenDTO(accessToken, refreshToken);

        return tokens;
    }

    public Claims getClaims(String token) {
        try {
            return (Claims) Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parse(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}

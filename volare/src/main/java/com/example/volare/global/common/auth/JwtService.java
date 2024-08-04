package com.example.volare.global.common.auth;

import com.example.volare.global.apiPayload.code.status.ErrorStatus;
import com.example.volare.global.apiPayload.exception.handler.GeneralHandler;
import com.example.volare.global.common.auth.model.TokenDTO;
import com.example.volare.global.common.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService implements InitializingBean {
    private static final String email = "usermail";
    private static SecretKey secretKey;
    private final UserDetailService userDetailService;
    private final AuthRedisService authRedisService;

    @Override
    public void afterPropertiesSet(){
        // jwt 시크릿 키 설정
        secretKey = JwtConfig.RANDOM_SECRET_KEY;
    }

    public TokenDTO createToken(String userEmail) {
        Date now = new Date();

        String accessToken = Jwts.builder()
                .expiration(new Date(now.getTime()+ JwtConfig.ACCESS_TOKEN_VALID_TIME))
                .subject("access-token")
                .claim(email, userEmail)
                .issuedAt(now)
                .signWith(secretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .expiration(new Date(now.getTime() + JwtConfig.REFRESH_TOKEN_VALID_TIME))
                .subject("refresh-token")
                .claim(email, userEmail)
                .issuedAt(now)
                .signWith(secretKey)
                .compact();

        TokenDTO tokens = new TokenDTO(accessToken, refreshToken);

        return tokens;
    }

    public TokenDTO createAndSaveTokens(String userEmail) {
        TokenDTO tokens = createToken(userEmail);
        // 업데이트 로직 필요
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

    public boolean checkValidationToken(String token) {
        try {
            Claims claims = getClaims(token);
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                throw new ExpiredJwtException(null, null, "Token has expired");
            }
            // 블랙리스트를 통해 로그아웃 후 기존 토큰 탈취를 통한 접근을 방지
            ValueOperations<String, String> logoutValueOperations = authRedisService.getRedisTemplate().opsForValue();
            if (logoutValueOperations.get("blackList"+token) != null) {
                return false;
            }
            return true;
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        String userEmail = (String) getClaims(token).get(email);
        UserDetails user = userDetailService.loadUserByUsername(userEmail);
        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }

    @Transactional
    public TokenDTO reissueToken(String accessToken, String refreshToken) {
        String accessEmail = null;

        try {
            // accessToken 만료 전, refreshToken 요청이 오면 토큰 정보 탈취 위험성이 있어, 모든 토큰 재발급
            accessEmail = (String) getClaims(accessToken).get(email);
        } catch (ExpiredJwtException e) {
            accessEmail = String.valueOf(e.getClaims().get(email));
        }

        try {
            getClaims(refreshToken);

            // Redis 사용 시에는 refreshToken 유효 기간 검증 필요 없음 -> 시간이 지난 후 삭제됨
            String value = authRedisService.getValues(refreshToken)
                    .orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));
            if(!value.equals(accessEmail)) {
                throw new GeneralHandler(ErrorStatus._BAD_REQUEST);
            }

            // 기존 refresh token 삭제 후 재생성
            authRedisService.deleteValues(refreshToken);
            TokenDTO newTokens = createAndSaveTokens(value);

            return newTokens;
        } catch (ExpiredJwtException e) {
            throw new GeneralHandler(ErrorStatus._BAD_REQUEST);
        }
    }
}

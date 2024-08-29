package com.example.volare.service;

import com.example.volare.dto.UserDTO;
import com.example.volare.global.common.auth.AuthRedisService;
import com.example.volare.global.common.auth.JwtService;
import com.example.volare.model.User;
import com.example.volare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthRedisService authRedisService;
    private final JwtService jwtService;

    public void signOut(String accessToken, String refreshToken) {
        long expiredAccessTokenTime = jwtService.getClaims((accessToken)).getExpiration().getTime() - new Date().getTime();
        authRedisService.setValuesWithTimeout("blackList" + accessToken, accessToken,expiredAccessTokenTime);
        authRedisService.deleteValues(refreshToken);
    }

    public UserDTO getUserInfo(User user){
        return UserDTO.builder()
                .email(user.getEmail())
                .build();
    }

}

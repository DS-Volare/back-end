package com.example.volare.service;

import com.example.volare.global.common.auth.AuthRedisService;
import com.example.volare.model.User;
import com.example.volare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthRedisService authRedisService;

    public void signOut(User user, String refreshToken) {
        authRedisService.deleteValues(refreshToken);
    }

}

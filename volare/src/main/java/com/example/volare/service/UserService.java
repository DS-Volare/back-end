package com.example.volare.service;

import com.example.volare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

//    public void signOut(User user, String refreshToken) {
//        jwtService.matchCheckTokens(user.getUserId(), refreshToken);
//        userRepository.deleteValues(refreshToken);
//    }

}

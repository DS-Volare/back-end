package com.example.volare.global.common.auth;

import com.example.volare.global.common.auth.model.TokenDTO;
import com.example.volare.global.common.config.JwtConfig;
import com.example.volare.model.User;
import com.example.volare.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final HttpSession httpSession;
    private final AuthRedisService authRedisService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 로그인 진행 중인 서비스를 구분
        // 네이버로 로그인 진행 중인지, 구글로 로그인 진행 중인지
        // provider - string to enum으로 변환
        User.SocialType provider = User.SocialType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
        String userNameAttribute = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint()
                .getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(provider, userNameAttribute, oAuth2User.getAttributes());

        saveUser(attributes,provider);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private void saveUser(OAuthAttributes attributes, User.SocialType provider) {
        Optional<User> existingUser = userRepository.findByEmail(attributes.getEmail());

        if (existingUser.isPresent()) {
            // 이미 가입된 사용자인 경우
            //case1) 로그아웃으로 refreshToken인 없는 상태 -> 토큰 재발급 필요함 == 기존 회원 로그인 시도(정상 요청)
            saveToken(attributes.getEmail());
               //case2) 논리적 유저 플로우 불가능 상황 : 로그아웃 전(이미 로그인 상태)에서 로그인 시도 -> 에러 발생 필요 (비정상 요청)
               //case3) 개발자 accessToken 확인을 위한 비정상적인 루트로 접근한 경우(비정상 요청)

        } else {
            // 가입되지 않은 사용자 => User 엔티티 생성 후 저장
            saveToken(attributes.getEmail());
            User newUser = User.builder()
                    .email(attributes.getEmail())
                    .picture(attributes.getPicture())
                    .socialType(provider)
                    .build();
            userRepository.save(newUser);
        }
    }

    private void saveToken(String email){
        // 토큰 생성
        TokenDTO tokens = jwtService.createToken(email);

        //세션에 저장
        httpSession.setAttribute("accessToken", tokens.getAccessToken());
        httpSession.setAttribute("refreshToken", tokens.getRefreshToken());

        //access-token 및 refresh-token 저장
        authRedisService.setValuesWithTimeout(tokens.getRefreshToken(), String.valueOf(email), JwtConfig.REFRESH_TOKEN_VALID_TIME);

    }
}
package com.example.volare.global.common.auth;

import com.example.volare.global.apiPayload.code.status.ErrorStatus;
import com.example.volare.global.apiPayload.exception.handler.TempHandler;
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

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 로그인 진행 중인 서비스를 구분
        // 네이버로 로그인 진행 중인지, 구글로 로그인 진행 중인지
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // OAuth2 로그인 진행 시 키가 되는 필드 값(Primary Key와 같은 의미)
        // 구글의 경우 기본적으로 코드를 지원
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // OAuth2UserService를 통해 가져온 OAuth2User의 attribute 등을 담을 클래스
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        // 사용자 저장 또는 업데이트
        User user = saveOrUpdate(attributes);

        // 토큰 생성 및 세션에 저장
        String userUUID = String.valueOf(user.getId());
        TokenDTO tokens = jwtService.createToken(userUUID);
        httpSession.setAttribute("accessToken", tokens.getAccessToken());
        httpSession.setAttribute("refreshToken", tokens.getRefreshToken());

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        Optional<User> existingUser = userRepository.findByEmail(attributes.getEmail());

        if (existingUser.isPresent()) {
            // 이미 가입된 사용자인 경우 예외 발생
            throw new TempHandler(ErrorStatus._BAD_REQUEST);
        } else {
            // 가입되지 않은 사용자 => User 엔티티 생성 후 저장
            User newUser = attributes.toEntity();
            // 토큰 생성
            return userRepository.save(newUser);
        }
    }
}
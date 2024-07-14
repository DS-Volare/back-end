package com.example.volare.global.common.auth;

import com.example.volare.global.common.auth.model.TokenDTO;
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
        // provider - string to enum으로 변환
        User.SocialType provider = User.SocialType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
        String userNameAttribute = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint()
                .getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(provider, userNameAttribute, oAuth2User.getAttributes());

        User user = saveUser(attributes,provider);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private User saveUser(OAuthAttributes attributes, User.SocialType provider) {
        Optional<User> existingUser = userRepository.findByEmail(attributes.getEmail());

        if (existingUser.isPresent()) {
            // 이미 가입된 사용자인 경우 예외 발생
            throw new OAuth2AuthenticationException("기존 회원입니다. 소셜 계정을 확인해주세요!");
        } else {
            // 가입되지 않은 사용자 => User 엔티티 생성 후 저장

            // 토큰 생성 및 세션에 저장
            String userUUID = String.valueOf(attributes.getEmail());
            TokenDTO tokens = jwtService.createToken(userUUID);
            httpSession.setAttribute("accessToken", tokens.getAccessToken());
            httpSession.setAttribute("refreshToken", tokens.getRefreshToken());

            //Todo: 저장 장소 변경 리팩토링

            User newUser = User.builder()
                    .email(attributes.getEmail())
                    .picture(attributes.getPicture())
                    .accessToken(tokens.getAccessToken())
                    .refreshToken(tokens.getRefreshToken())
                    .socialType(provider)
                    .build();
            return userRepository.save(newUser);
        }

    }
}
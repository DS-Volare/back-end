package com.example.volare.global.common.config;

import com.example.volare.global.common.auth.CustomAuthenticationSuccessHandler;
import com.example.volare.global.common.auth.CustomOAuth2UserService;
import com.example.volare.global.common.auth.JwtAuthenticationFilter;
import com.example.volare.global.common.auth.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final JwtService jwtService;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.disable()) // CORS 비활성화 (필요시 커스터마이징 가능)
                .csrf(csrf -> csrf.disable()) // CSRF 보호 기능 비활성
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorizeRequest) -> authorizeRequest
                        .requestMatchers("/", "/css/**", "images/**", "/js/**", "/login/*", "/logout/*").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth -> oauth // OAuth2 로그인 설정
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)) // 사용자 서비스 설정
                        //.failureHandler(oAuth2LoginFailureHandler) // 로그인 실패 핸들러
                        .successHandler(customAuthenticationSuccessHandler) // 로그인 성공 핸들러
                );


        return http.build();
    }
}
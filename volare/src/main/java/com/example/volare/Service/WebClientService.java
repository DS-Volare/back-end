package com.example.volare.Service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WebClientService {

    // webClient 기본 설정
    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:5000")
            .build();

    public void ScriptConverter (String text) {
        // POST 요청 보내기
        webClient.post()
                .uri("/convert_script")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(text))
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> {
                    // 응답 처리
                    System.out.println("응답: " + response);
                })
                .doOnError(error -> {
                    // 오류 처리
                    System.err.println("오류 발생: " + error.getMessage());
                })
                .subscribe();
    }
}

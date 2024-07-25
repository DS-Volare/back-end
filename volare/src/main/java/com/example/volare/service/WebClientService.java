package com.example.volare.service;

import com.example.volare.dto.ScriptDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Service
public class WebClientService {

    // webClient 기본 설정
    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:5000")
            .clientConnector(new ReactorClientHttpConnector(
                    HttpClient.create().followRedirect(true)
            ))
            .codecs(configurer -> configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(new ObjectMapper())))
            .build();

    // 소설 변환 api 호출
    public Mono<ScriptDTO.NovelToStoryScriptResponseDTO> convertStoryBord(ScriptDTO.ScriptRequestDTO changeNovel) throws JsonProcessingException {

        return this.webClient.post()
                .uri("/convert_script")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(changeNovel)
                .retrieve()// 응답받기
                .bodyToMono(ScriptDTO.NovelToStoryScriptResponseDTO.class)
                .doOnError(error -> {
                    // 오류 처리
                    // 외부 API Connection refused: no further information 오류는 클라이언트가 서버에 연결을 시도했지만, 서버가 요청을 수락하지 않거나 서버에 연결할 수 없다는
                });
    }
}

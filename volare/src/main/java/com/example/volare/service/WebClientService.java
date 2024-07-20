package com.example.volare.service;

import com.example.volare.dto.ScriptDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    // webClient 기본 설정
    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:5000")
            .clientConnector(new ReactorClientHttpConnector(
                    HttpClient.create().followRedirect(true)
            ))
            .codecs(configurer -> configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(new ObjectMapper())))
            .build();

    // 소설 변환 api 호출
//    public Mono<ScriptDTO.NovelToStoryScriptResponseDTO> convertStoryBord(ScriptDTO.ScriptRequestDTO changeNovel) throws JsonProcessingException {

//
public void convertStoryBord(ScriptDTO.ScriptRequestDTO changeNovel) throws JsonProcessingException {
        String jsonString = objectMapper.writeValueAsString(changeNovel);
        log.info("Request payload: {}", jsonString);

        this.webClient.post()
                .uri("/convert_script")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(changeNovel)
                .retrieve()
                .bodyToMono(String.class) // 응답을 문자열로 받기
                .subscribe(responseBody -> {
                    // 응답을 로그에 출력
                    log.info("Response body: {}", responseBody);

                    // 응답 JSON을 파싱
                    try {
                        JsonNode jsonNode = objectMapper.readTree(responseBody);
                        log.info("Parsed JSON Node: {}", jsonNode);

                        // JSON 구조 확인
                        if (jsonNode.has("script")) {
                            JsonNode scriptNode = jsonNode.get("script");
                            log.info("Script Node: {}", scriptNode);

                            // script 내용 출력
                            JsonNode sceneNode = scriptNode.get("scene");
                            log.info("Scene Node: {}", sceneNode);

                            // JSON 데이터를 처리하고 필요한 정보를 로그에 출력
                            for (JsonNode scene : sceneNode) {
                                JsonNode contentNode = scene.get("content");
                                log.info("Content Node: {}", contentNode);

                                for (JsonNode content : contentNode) {
                                    String action = content.get("action").asText();
                                    String character = content.get("character").asText();
                                    String dialog = content.get("dialog").asText();
                                    String type = content.get("type").asText();

                                    log.info("Action: {}, Character: {}, Dialog: {}, Type: {}", action, character, dialog, type);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("Error during JSON processing: ", e);
                    }
                }, error -> {
                    // 오류 처리
                    log.error("Error during API call: ", error);
                });

    }
}

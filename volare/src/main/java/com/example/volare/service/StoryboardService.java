package com.example.volare.service;

import com.example.volare.dto.StoryboardDTO;
import com.example.volare.global.apiPayload.code.status.ErrorStatus;
import com.example.volare.global.apiPayload.exception.handler.GeneralHandler;
import com.example.volare.model.Novel;
import com.example.volare.model.Script;
import com.example.volare.model.StoryBoard;
import com.example.volare.repository.NovelRepository;
import com.example.volare.repository.ScriptRepository;
import com.example.volare.repository.StoryBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoryboardService {

    private final String aiServerUrl = "http://75.63.212.242:44809/convert_storyboard/";
    private final ScriptRepository scriptRepository;
    private final StoryBoardRepository storyBoardRepository;
    private final NovelRepository novelRepository;

    public StoryboardDTO.Response generateStoryboard(StoryboardDTO.Request request) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(Collections.singletonList(new MappingJackson2HttpMessageConverter()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<StoryboardDTO.Request> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<StoryboardDTO.Response> response = restTemplate.exchange(
                    aiServerUrl, HttpMethod.POST, entity, StoryboardDTO.Response.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                log.error("Failed to generate storyboard: Status code - {}", response.getStatusCode());
                throw new RuntimeException("Failed to generate storyboard: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("HTTP error: Status code - {}, Response body - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("HTTP error occurred while generating storyboard", e);
        } catch (Exception e) {
            log.error("Unexpected error: ", e);
            throw new RuntimeException("An unexpected error occurred while generating storyboard", e);
        }
    }

    public StoryboardDTO.Response saveStoryboard(Long scriptId, StoryboardDTO.Request request) {
        Script script = scriptRepository.findById(scriptId)
                .orElseThrow(() -> new RuntimeException("Script not found with id: " + scriptId));

        // Generate the storyboard
        StoryboardDTO.Response storyboardResponse = generateStoryboard(request);

        // Convert the DTO to entities and save to the database
        List<StoryBoard> storyBoards = storyboardResponse.getScene().stream()
                .map(scene -> StoryBoard.builder()
                        .script(script)
                        .sceneNum(scene.getScene_num())
                        .locate(scene.getLocation())
                        .time(scene.getTime())
                        .summary(scene.getCuts().stream()
                                .map(cut -> cut.getCut_num() + ": " + cut.getText())
                                .collect(Collectors.joining("\n")))
                        .build())
                .collect(Collectors.toList());

        storyBoardRepository.saveAll(storyBoards);

        /* 스토리보드가 저장된 후, 스토리보다가 속한 Novel의 수정 시간 갱신*/
        Novel novel = script.getNovel();
        novel.updateTimestamp(LocalDateTime.now());
        novelRepository.save(novel);


        return storyboardResponse;
    }

    // 스토리보드 정보조회
    public StoryboardDTO.Response getStoryBoardDetail(Long storyboardId){
        StoryBoard storyBoard = storyBoardRepository.findById(storyboardId).orElseThrow(()-> new GeneralHandler(ErrorStatus._BAD_REQUEST));
        return StoryboardDTO.storyBoardConvertToDto(storyBoard);
    }

}
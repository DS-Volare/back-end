package com.example.volare.service;

import com.example.volare.dto.StoryboardDTO;
import com.example.volare.global.apiPayload.code.status.ErrorStatus;
import com.example.volare.global.apiPayload.exception.handler.GeneralHandler;
import com.example.volare.model.Novel;
import com.example.volare.model.Script;
import com.example.volare.model.StoryBoard;
import com.example.volare.model.StoryBoardCut;
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
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoryboardService {

    private final String aiServerUrl = "http://97.83.103.94:4050/convert_storyboard/";
    private final ScriptRepository scriptRepository;
    private final StoryBoardRepository storyBoardRepository;
    private final NovelRepository novelRepository;

    // AI 모델에 요청을 보내서 스토리보드를 생성하는 메소드
    public StoryboardDTO.Response generateStoryboard(StoryboardDTO.Request request) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(List.of(new MappingJackson2HttpMessageConverter()));

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

    // 스토리보드를 생성하고 데이터베이스에 저장하는 메소드
    public StoryboardDTO.Response saveStoryboard(Long scriptId, StoryboardDTO.Request request) {
        Script script = scriptRepository.findById(scriptId)
                .orElseThrow(() -> new RuntimeException("Script not found with id: " + scriptId));

        // '지문' 타입의 씬을 제외하고 AI 모델에 전달할 씬을 필터링
        List<StoryboardDTO.Request.Script.Scene> filteredScenes = request.getScript().getScene().stream()
                .map(scene -> StoryboardDTO.Request.Script.Scene.builder()
                        .scene_num(scene.getScene_num())
                        .location(scene.getLocation())
                        .content(scene.getContent().stream()
                                .filter(content -> !"지문".equals(content.getType())) // '지문' 타입 필터링
                                .collect(Collectors.toList())) // 필터링된 컨텐츠 추가
                        .build())
                .collect(Collectors.toList());

        // 필터링된 Script 객체 생성
        StoryboardDTO.Request.Script filteredScript = StoryboardDTO.Request.Script.builder()
                .scene(filteredScenes)
                .build();

        // 새로운 요청 객체 생성 (필터링된 씬 사용)
        StoryboardDTO.Request filteredRequest = StoryboardDTO.Request.builder()
                .scriptId(request.getScriptId())
                .script(filteredScript)
                .build();

        // 필터링된 씬으로 AI 모델에 스토리보드 생성 요청
        StoryboardDTO.Response storyboardResponse = generateStoryboard(filteredRequest);

        // AI에서 반환된 storyboardResponse 데이터를 사용하여 스토리보드 저장
        List<StoryBoard> storyBoards = storyboardResponse.getScene().stream()
                .map(scene -> {
                    // StoryBoard 객체 생성 (여기서 먼저 생성)
                    StoryBoard storyBoard = StoryBoard.builder()
                            .script(script)
                            .sceneNum(scene.getScene_num())
                            .locate(scene.getLocation())
                            .time(scene.getTime())
                            .summary(scene.getCuts().stream()
                                    .map(cut -> {
                                        // 각 cut에 대해 처리
                                        String text = cut.getCut_num() + ": " + cut.getText();
                                        return text;
                                    })
                                    .collect(Collectors.joining("\n"))) // 여러 cut을 하나의 요약으로 결합
                            .build();

                    // StoryBoardCut 리스트 생성
                    List<StoryBoardCut> cuts = scene.getCuts().stream()
                            .map(cut -> {
                                // StoryBoardCut 객체 생성
                                StoryBoardCut storyBoardCut = StoryBoardCut.builder()
                                        .cutImage(cut.getCutImage()) // cutImage가 있다면 추가
                                        .cutNum(cut.getCut_num())
                                        .text(cut.getText())
                                        .build();

                                // StoryBoard와 연결
                                storyBoardCut.setStoryBoard(storyBoard); // 스토리보드와 연결
                                return storyBoardCut;
                            })
                            .collect(Collectors.toList());

                    // StoryBoard에 cuts 설정
                    storyBoard.setCuts(cuts); // 생성한 cuts 리스트 추가

                    return storyBoard; // 스토리보드 객체 반환
                })
                .collect(Collectors.toList());

        // 스토리보드를 데이터베이스에 저장
        storyBoardRepository.saveAll(storyBoards);

        // 소속된 Novel의 수정 시간을 갱신
        Novel novel = script.getNovel();
        novel.updateTimestamp(LocalDateTime.now());
        novelRepository.save(novel);

        return storyboardResponse;
    }


    // 스토리보드 정보 조회
    public StoryboardDTO.Response getStoryBoardDetail(Long storyboardId) {
        StoryBoard storyBoard = storyBoardRepository.findById(storyboardId)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));
        return StoryboardDTO.storyBoardConvertToDto(storyBoard);
    }
}

package com.example.volare.service;


import com.example.volare.dto.ScriptDTO;
import com.example.volare.dto.StatisticsDTO;
import com.example.volare.global.apiPayload.code.status.ErrorStatus;
import com.example.volare.global.apiPayload.exception.handler.GeneralHandler;
import com.example.volare.model.Novel;
import com.example.volare.model.Script;
import com.example.volare.model.User;
import com.example.volare.repository.NovelRepository;
import com.example.volare.repository.ScriptRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import com.example.volare.model.ScriptScene;



@Slf4j
@Service
@RequiredArgsConstructor
public class ScriptService {
    private final WebClientService webClientService;
    private final NovelRepository novelRepository;
    private final ScriptRepository scriptRepository;

    // SAMPLE 조회
    @Cacheable(cacheNames = "SAMPLE_SCRIPTS" , key = "#sampleTag")
    public ScriptDTO.SampleScriptResponseDTO getSampleScript(String sampleTag){
        Script script = scriptRepository.findByType(sampleTag).orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));
        return new ScriptDTO.SampleScriptResponseDTO(script.getScriptFile());
    }



    // 스크립트 결과 DB 저장

    //TODO: 속도 테스트를 위함(1) ->비동기 호출 비동기 저장- 완료 후 return
    /*
    public Mono<Script> saveStoryScript(String  novelId, User user,ScriptDTO.ScriptRequestDTO changeNovel) throws JsonProcessingException {
        // Novel
        //novelRepository.findById(novelId).orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));

        Mono<ScriptDTO.NovelToStoryScriptResponseDTO> novelToStoryScriptResponseDTOMono = webClientService.convertScript(changeNovel);
        return novelToStoryScriptResponseDTOMono
                .map(this::convertToEntity)
                .flatMap(entity -> Mono.fromCallable(() -> scriptRepository.save(entity))
                        .subscribeOn(Schedulers.boundedElastic())); // 비동기 실행
    }
     */


    //TODO: 속도 테스트를 위함(2) -> 동기식 저장 , 동기식 호출

    public ScriptDTO.NovelToStoryScriptResponseDTO saveStoryScript(String  novelId, User user, ScriptDTO.ScriptRequestDTO changeNovel) throws JsonProcessingException {
        // Novel
        Novel novel = novelRepository.findById(novelId).orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));

        // WebClient 호출을 동기식으로 처리
        ScriptDTO.NovelToStoryScriptResponseDTO novelToStoryScriptResponseDTO = webClientService.convertScript(changeNovel).block();

        // 결과+ 등장인물 정보 엔티티 변환
        Script entity = ScriptDTO.convertToEntity(novel,novelToStoryScriptResponseDTO, changeNovel.getCandidates());

        // 동기식으로 DB에 저장
        scriptRepository.save(entity);

        /* 스크립트 저장된 후, 스크립트가 속한 Novel의 수정 시간 갱신*/
        novel.updateTimestamp(LocalDateTime.now());
        novelRepository.save(novel);


        // DTO 계층 사용
        return ScriptDTO.EntityToDTO(entity);
    }



    //TODO: 속도 테스트를 위함(3) -> 동기적 호출, 비동기적 저장 - 완료 전 return
    /*
    public ScriptDTO.NovelToStoryScriptResponseDTO saveStoryScript(String  novelId, User user, ScriptDTO.ScriptRequestDTO changeNovel) throws JsonProcessingException {
        // User 검증 로직 -  현재 팀 계정을 운영하지 않음으로 user검증은 JWT로 회원유저인지 확인하는 로직으로 대체
        // Novel
        novelRepository.findById(novelId).orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));

        // 웹 클라이언트 호출을 동기적으로 처리
        ScriptDTO.NovelToStoryScriptResponseDTO responseDTO = webClientService.convertScript(changeNovel).block();

        // 비동기적으로 저장 로직 수행
        Mono.fromCallable(() -> {
                    //log.info("DB save task started");
                    Script entity = ScriptDTO.convertToEntity(responseDTO);
                    scriptRepository.save(entity);
                    //log.info("DB save task completed");
                    return entity;
                })
                .subscribeOn(Schedulers.boundedElastic()) // 비동기 실행
                .subscribe();

        //log.info("Returning responseDTO");
        // 스크립트 결과 FE 반환
        return responseDTO;
    }
    */

    public StatisticsDTO.MindMapDTO getScriptInfo(Long scriptId) {
        // 스크립트 존재 여부 검증
        Script script = scriptRepository.findById(scriptId)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));

        // 스크립트 제목 추출
        String title = script.getNovel().getTitle();

        // 스크립트 내 모든 location 추출
        List<String> locations = script.getScriptScenes().stream()
                .map(ScriptScene::getLocation)
                .distinct()
                .collect(Collectors.toList());

        // 스크립트 내 모든 등장인물 추출
        List<String> characters = script.getCharacters().stream()
                .distinct()
                .collect(Collectors.toList());

        // DTO로 변환하여 반환
        return StatisticsDTO.MindMapDTO.builder()
                .title(title)
                .locations(locations)
                .characters(characters)
                .build();
    }

    public List<Map<String, Object>> updateScriptItems(List<Map<String, Object>> uList, int sceneNumber, int contentIndex, String character, String action, String dialog) {
        for (Map<String, Object> item : uList) {
            int currentSceneNumber = ((Number) item.get("sceneNumber")).intValue();
            int currentContentIndex = ((Number) item.get("contentIndex")).intValue();

            // sceneNumber와 contentIndex가 일치하는 항목을 찾아서 업데이트
            if (currentSceneNumber == sceneNumber && currentContentIndex == contentIndex) {
                if (character != null) {
                    item.put("character", character);
                }
                if (action != null) {
                    item.put("action", action);

                }
                if (dialog != null) {
                    item.put("dialog", dialog);
                }
                break;
            }
        }
        return uList;
    }


    // Script 데이터를 txt 파일로 변환하는 메서드 추가
    public Path generateScriptTxtFile(Long scriptId) throws IOException {
        // Script 조회
        Optional<Script> optionalScript = scriptRepository.findById(scriptId);
        if (optionalScript.isEmpty()) {
            throw new GeneralHandler(ErrorStatus._BAD_REQUEST);
        }

        Script script = optionalScript.get();

        // 파일 경로 설정
        Path filePath = Paths.get("script_" + scriptId + ".txt");

        // Script 데이터를 텍스트 파일에 쓰기
        StringBuilder sb = new StringBuilder();
        sb.append("Script ID: ").append(script.getId()).append("\n");
        sb.append("Novel Title: ").append(script.getNovel().getTitle()).append("\n");
        sb.append("Type: ").append(script.getType()).append("\n");
        sb.append("Script File Content: \n").append(script.getScriptFile()).append("\n");
        sb.append("Characters: \n");

        for (String character : script.getCharacters()) {
            sb.append("- ").append(character).append("\n");
        }

        sb.append("Scenes: \n");
        for (ScriptScene scene : script.getScriptScenes()) {
            sb.append("Scene Location: ").append(scene.getLocation()).append("\n");
            // 필요한 경우 더 많은 Scene 정보를 출력할 수 있습니다.
        }

        Files.write(filePath, sb.toString().getBytes());

        return filePath;
    }

    // 파일 다운로드를 위한 API 응답 처리
    public ResponseEntity<InputStreamResource> downloadScriptTxtFile(Long scriptId) throws IOException {
        // 텍스트 파일 생성
        Path filePath = generateScriptTxtFile(scriptId);

        // 파일을 InputStream으로 읽어들임
        InputStreamResource resource = new InputStreamResource(new FileInputStream(filePath.toFile()));

        // 파일 다운로드 응답 반환
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filePath.getFileName())
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(filePath.toFile().length())
                .body(resource);
    }



    // 대본 정보조회
    public ScriptDTO.ScriptDetailResponseDTO getScriptDetail(Long scriptId){
        Script script = scriptRepository.findById(scriptId)
                .orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));
        return ScriptDTO.scriptConvertToDto(script);
    }

}

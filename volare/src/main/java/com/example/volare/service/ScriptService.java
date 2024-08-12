package com.example.volare.service;


import com.example.volare.dto.ScriptDTO;
import com.example.volare.global.apiPayload.code.status.ErrorStatus;
import com.example.volare.global.apiPayload.exception.handler.GeneralHandler;
import com.example.volare.model.Script;
import com.example.volare.model.User;
import com.example.volare.repository.NovelRepository;
import com.example.volare.repository.ScriptRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


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
    public Mono<Script> saveStoryScript(String novelId, User user,ScriptDTO.ScriptRequestDTO changeNovel) throws JsonProcessingException {
        // Novel
        //novelRepository.findById(novelId).orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));

        Mono<ScriptDTO.NovelToStoryScriptResponseDTO> novelToStoryScriptResponseDTOMono = webClientService.convertStoryBord(changeNovel);
        return novelToStoryScriptResponseDTOMono
                .map(this::convertToEntity)
                .flatMap(entity -> Mono.fromCallable(() -> scriptRepository.save(entity))
                        .subscribeOn(Schedulers.boundedElastic())); // 비동기 실행
    }
     */


    //TODO: 속도 테스트를 위함(2) -> 동기식 저장 , 동기식 호출

    public ScriptDTO.NovelToStoryScriptResponseDTO saveStoryScript(String novelId, User user, ScriptDTO.ScriptRequestDTO changeNovel) throws JsonProcessingException {
        // Novel
        //novelRepository.findById(novelId).orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));

        // WebClient 호출을 동기식으로 처리
        ScriptDTO.NovelToStoryScriptResponseDTO novelToStoryScriptResponseDTO = webClientService.convertStoryBord(changeNovel).block();

        // 결과를 엔티티로 변환
        Script entity = ScriptDTO.convertToEntity(novelToStoryScriptResponseDTO);

        // 동기식으로 DB에 저장
        scriptRepository.save(entity);
        // DTO 계층 사용
        return ScriptDTO.EntityToDTO(entity);
    }



    //TODO: 속도 테스트를 위함(3) -> 동기적 호출, 비동기적 저장 - 완료 전 return
    /*
    public ScriptDTO.NovelToStoryScriptResponseDTO saveStoryScript(String novelId, User user, ScriptDTO.ScriptRequestDTO changeNovel) throws JsonProcessingException {
        // User 검증 로직 -  현재 팀 계정을 운영하지 않음으로 user검증은 JWT로 회원유저인지 확인하는 로직으로 대체
        // Novel
        novelRepository.findById(novelId).orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));

        // 웹 클라이언트 호출을 동기적으로 처리
        ScriptDTO.NovelToStoryScriptResponseDTO responseDTO = webClientService.convertStoryBord(changeNovel).block();

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

}

package com.example.volare.service;

import com.example.volare.dto.ScriptDTO;
import com.example.volare.model.Script;
import com.example.volare.repository.NovelRepository;
import com.example.volare.repository.ScriptRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScriptService {
    private final WebClientService webClientService;
    private final NovelRepository novelRepository;
    private final ScriptRepository scriptRepository;


    // 스크립트 결과 DB 저장
    //public Mono<Script> saveStoryScript(ScriptDTO.ScriptRequestDTO changeNovel) throws JsonProcessingException {
    public void saveStoryScript(ScriptDTO.ScriptRequestDTO changeNovel) throws JsonProcessingException {

        webClientService.convertStoryBord(changeNovel);
//        return webClientService.convertStoryBord(changeNovel)
//                .map(this::convertToEntity)
//                .flatMap(entity -> Mono.fromCallable(() -> scriptRepository.save(entity))
//                        .subscribeOn(Schedulers.boundedElastic())); // 비동기 스레드에서 JPA 작업 수행
    }

    // 스크립트 결과 FE 반환

    //
    public Script convertToEntity(ScriptDTO.NovelToStoryScriptResponseDTO storyScript){

        Script script = Script.builder()
                .scriptFile(storyScript.getScript_str()) // script_str을 scriptFile에 설정
                .locates(storyScript.getScript().getScene().getLocation()) // location을 locates에 설정
                .sceneNum(storyScript.getScript().getScene().getScene_num()) // sceneNum을 sceneNum에 설정
                .time(storyScript.getScript().getScene().getTime()) // time을 time에 설정
                .contents(storyScript.getScript().getScene().getContent().stream()
                        .map(content -> Script.Content.builder()
                                .action(content.getAction())
                                .character(content.getCharacter())
                                .dialog(content.getDialog())
                                .build()
                        )
                        .collect(Collectors.toList())
                )
                .build();
        return  script;
    }

}

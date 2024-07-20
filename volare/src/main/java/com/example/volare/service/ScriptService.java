package com.example.volare.service;

import com.example.volare.dto.ScriptDTO;
import com.example.volare.model.Script;
import com.example.volare.repository.NovelRepository;
import com.example.volare.repository.ScriptRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScriptService {
    private final WebClientService webClientService;
    private final NovelRepository novelRepository;
    private final ScriptRepository scriptRepository;


    // 스크립트 결과 DB 저장
    public Script saveStoryScript(ScriptDTO.ScriptRequestDTO changeNovel) throws JsonProcessingException {

//        Mono<ScriptDTO.NovelToStoryScriptResponseDTO> novelToStoryScriptResponseDTOMono = webClientService.convertStoryBord(changeNovel);
//        log.info(novelToStoryScriptResponseDTOMono.toString());
//       return novelToStoryScriptResponseDTOMono
//                .map(this::convertToEntity)
//                .flatMap(entity -> Mono.fromCallable(() -> scriptRepository.save(entity)));
        // WebClient 호출을 동기식으로 처리
        ScriptDTO.NovelToStoryScriptResponseDTO novelToStoryScriptResponseDTO = webClientService.convertStoryBord(changeNovel).block();

        // 결과를 엔티티로 변환
        Script entity = convertToEntity(novelToStoryScriptResponseDTO);

        // 동기식으로 DB에 저장
        return scriptRepository.save(entity);
    }

    // 스크립트 결과 FE 반환

    //
    public Script convertToEntity(ScriptDTO.NovelToStoryScriptResponseDTO storyScript){

        Script script = Script.builder()
                .scriptFile(storyScript.getScript_str()) // script_str을 scriptFile에 설정
                .locates(storyScript.getScript().getScene().get(0).getLocation()) // location을 locates에 설정
                .sceneNum(storyScript.getScript().getScene().get(0).getScene_num()) // sceneNum을 sceneNum에 설정
                .time(storyScript.getScript().getScene().get(0).getTime()) // time을 time에 설정
                .contents(storyScript.getScript().getScene().get(0).getContent().stream()
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

package com.example.volare.controller;

import com.example.volare.global.apiPayload.ApiResponse;
import com.example.volare.global.common.auth.model.AuthUser;
import com.example.volare.service.ScriptService;
import com.example.volare.dto.ScriptDTO;
import com.example.volare.model.Script;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("scripts")
public class ScriptController {
    private final ScriptService scriptService;

    // SAMPLE 변환 조회
    //TODO: 반환 값에 대해 FE 회의 진행 - String(sample, 일반 구분 컬럼 추가) or Object json(scriptFile 태그 검색)
    @GetMapping("sample/{sampleTag}")
    public ApiResponse<ScriptDTO.NovelToStoryScriptResponseDTO> getSampleScripts(@PathVariable String sampleTag){
        return ApiResponse.onSuccess();
    }


    // 소설 변환
    @PostMapping("/{novelId}")
    public ApiResponse<ScriptDTO.NovelToStoryScriptResponseDTO> saveData(
            @PathVariable String novelId,
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody ScriptDTO.ScriptRequestDTO req) throws JsonProcessingException {
        ScriptDTO.NovelToStoryScriptResponseDTO storyScript = scriptService.saveStoryScript(novelId,authUser.getUser(), req);
        return ApiResponse.onSuccess(storyScript);
    }



    // 내 작업 소설 정보 보기
}

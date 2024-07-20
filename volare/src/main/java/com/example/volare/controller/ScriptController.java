package com.example.volare.controller;

import com.example.volare.global.common.auth.model.AuthUser;
import com.example.volare.service.ScriptService;
import com.example.volare.dto.ScriptDTO;
import com.example.volare.model.Script;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("scripts")
public class ScriptController {
    private final ScriptService scriptService;

    // 소설 저장

    // 소설 변환
    @PostMapping("")
    public  ScriptDTO.NovelToStoryScriptResponseDTO saveData(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody ScriptDTO.ScriptRequestDTO req) throws JsonProcessingException {
    return scriptService.saveStoryScript(authUser.getUser(),req);
    }



    // 내 작업 소설 정보 보기
}

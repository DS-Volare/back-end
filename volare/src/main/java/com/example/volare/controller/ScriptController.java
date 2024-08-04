package com.example.volare.controller;

import com.example.volare.dto.ScriptDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.example.volare.global.apiPayload.ApiResponse;
import com.example.volare.global.common.auth.model.AuthUser;
import com.example.volare.service.ScriptService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("scripts")
public class ScriptController {
    private final ScriptService scriptService;

    // SAMPLE 변환 조회
    //TODO: 반환 값에 대해 FE 회의 진행 - String(sample, 일반 구분 컬럼 추가) or Object json(scriptFile 태그 검색)
    @GetMapping("sample/{sampleTag}")
    public ApiResponse<ScriptDTO.SampleScriptResponseDTO> getSampleScripts(@PathVariable String sampleTag){
        ScriptDTO.SampleScriptResponseDTO sampleScript = scriptService.getSampleScript(sampleTag);
        return ApiResponse.onSuccess(sampleScript);
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

    @PutMapping("/update-dialog")
    public ScriptDTO.ModifyScriptDTO updateDialog(@RequestBody ScriptDTO.ModifyScriptDTO scriptDTO,
                                                  @RequestParam int sceneNumber,
                                                  @RequestParam String character,
                                                  @RequestParam String newDialog) {
        boolean updated = false;
        for (ScriptDTO.ModifyScriptDTO.SceneDTO scene : scriptDTO.getScene()) {
            log.info("Checking scene number: " + scene.getScene_num());
            if (scene.getScene_num() == sceneNumber) {
                for (ScriptDTO.ModifyScriptDTO.ContentDTO content : scene.getContent()) {
                    log.info("Checking character: " + content.getCharacter());
                    if (content.getCharacter().trim().equalsIgnoreCase(character.trim())) {
                        content.setDialog(newDialog);
                        updated = true;
                        log.info("Updated dialog for character: " + content.getCharacter());
                        break; // 일치하는 대사를 찾으면 루프 종료
                    } else {
                        log.info("No match for character: " + content.getCharacter().trim());
                    }
                }
            }
        }
        if (updated) {
            log.info("Dialog updated successfully");
        } else {
            log.warn("No matching dialog found to update");
        }
        return scriptDTO;
    }
}

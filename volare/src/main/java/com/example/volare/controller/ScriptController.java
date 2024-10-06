package com.example.volare.controller;


import com.example.volare.dto.ScriptDTO;
import com.example.volare.dto.StatisticsDTO;
import com.example.volare.global.apiPayload.ApiResponse;
import com.example.volare.global.common.auth.model.AuthUser;
import com.example.volare.service.ScriptSceneService;
import com.example.volare.service.ScriptService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/scripts")
public class ScriptController {
    private final ScriptService scriptService;
    private final ScriptSceneService scriptSceneService;

    // SAMPLE 변환 조회
    @GetMapping("sample/{sampleTag}")
    public ApiResponse<ScriptDTO.SampleScriptResponseDTO> getSampleScripts(@PathVariable String sampleTag){
        ScriptDTO.SampleScriptResponseDTO sampleScript = scriptService.getSampleScript(sampleTag);
        return ApiResponse.onSuccess(sampleScript);
    }

    @PostMapping("/{novelId}")
    public ApiResponse<ScriptDTO.NovelToStoryScriptResponseDTO> saveData(
            @PathVariable String  novelId,
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody ScriptDTO.ScriptRequestDTO req) throws JsonProcessingException {
        ScriptDTO.NovelToStoryScriptResponseDTO storyScript = scriptService.saveStoryScript(novelId, authUser.getUser(), req);
        return ApiResponse.onSuccess(storyScript);

    }

    @GetMapping("/{scriptId}/appearance-rate")
    public ApiResponse<StatisticsDTO.AppearanceRateDTO> getCharacterStatistics(
            @PathVariable Long scriptId, @AuthenticationPrincipal AuthUser authUser) {
        return ApiResponse.onSuccess(scriptSceneService.getCharacterStatistics(scriptId));
    }


    @PutMapping("/{scriptId}/update")
    public ResponseEntity<Map<String, Object>> updateScript(
            @PathVariable Long scriptId,
            @RequestParam int sceneNumber,
            @RequestParam int contentIndex,
            @RequestParam(required = false) String character,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String dialog,
            @RequestBody Map<String, Object> requestBody) {

        List<Map<String, Object>> uList = (List<Map<String, Object>>) requestBody.get("u_list");

        List<Map<String, Object>> updatedList = scriptService.updateScriptItems(uList, sceneNumber, contentIndex, character, action, dialog);

        Map<String, Object> response = new HashMap<>();
        response.put("script_id", scriptId);
        response.put("u_list", updatedList);

        return ResponseEntity.ok(response);
    }



    @GetMapping("/{scriptId}/details")
    public ApiResponse<StatisticsDTO.MindMapDTO> getScriptDetails(@PathVariable Long scriptId) {
        return ApiResponse.onSuccess(scriptService.getScriptInfo(scriptId));
    }

    // Script ID로 txt 파일 다운로드
    @GetMapping("/{scriptId}/download")
    public ResponseEntity<InputStreamResource> downloadScriptTxtFile(@PathVariable Long scriptId) throws IOException {
        return scriptService.downloadScriptTxtFile(scriptId);
    }
}

package com.example.volare.controller;


import com.example.volare.dto.AppearanceStatisticsDTO;
import com.example.volare.dto.ScriptDTO;
import com.example.volare.global.apiPayload.ApiResponse;
import com.example.volare.global.common.auth.model.AuthUser;
import com.example.volare.service.ScriptSceneService;
import com.example.volare.service.ScriptService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
            @PathVariable String novelId,
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody ScriptDTO.ScriptRequestDTO req) throws JsonProcessingException {
        ScriptDTO.NovelToStoryScriptResponseDTO storyScript = scriptService.saveStoryScript(novelId, authUser.getUser(), req);
        return ApiResponse.onSuccess(storyScript);

    }

    @GetMapping("/{scriptId}/appearance-rate")
    public ApiResponse<AppearanceStatisticsDTO> getCharacterStatistics(
            @PathVariable Long scriptId, @AuthenticationPrincipal AuthUser authUser
    ) {
        AppearanceStatisticsDTO response = scriptSceneService.getCharacterStatistics(scriptId);
        return ApiResponse.onSuccess(response);
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

        List<Map<String, Object>> updatedList = updateScriptItems(uList, sceneNumber, contentIndex, character, action, dialog);
  

        Map<String, Object> response = new HashMap<>();
        response.put("script_id", scriptId);
        response.put("u_list", updatedList);

        return ResponseEntity.ok(response);
    }

    private List<Map<String, Object>> updateScriptItems(List<Map<String, Object>> uList, int sceneNumber, int contentIndex, String character, String action, String dialog) {
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
}

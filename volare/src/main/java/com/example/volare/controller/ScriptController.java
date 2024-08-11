package com.example.volare.controller;


import org.springframework.http.ResponseEntity;
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

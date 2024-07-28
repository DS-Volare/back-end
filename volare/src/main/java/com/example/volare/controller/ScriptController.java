package com.example.volare.controller;

import com.example.volare.dto.ScriptDTO;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class ScriptController {

    private static final Logger logger = LoggerFactory.getLogger(ScriptController.class);

    @PutMapping("/update-dialog")
    public ScriptDTO updateDialog(@RequestBody ScriptDTO scriptDTO,
                                  @RequestParam int sceneNumber,
                                  @RequestParam String character,
                                  @RequestParam String newDialog) {
        boolean updated = false;
        for (ScriptDTO.SceneDTO scene : scriptDTO.getScene()) {
            logger.info("Checking scene number: " + scene.getScene_num());
            if (scene.getScene_num() == sceneNumber) {
                for (ScriptDTO.ContentDTO content : scene.getContent()) {
                    logger.info("Checking character: " + content.getCharacter());
                    if (content.getCharacter().trim().equalsIgnoreCase(character.trim())) {
                        content.setDialog(newDialog);
                        updated = true;
                        logger.info("Updated dialog for character: " + content.getCharacter());
                        break; // 일치하는 대사를 찾으면 루프 종료
                    } else {
                        logger.info("No match for character: " + content.getCharacter().trim());
                    }
                }
            }
        }
        if (updated) {
            logger.info("Dialog updated successfully");
        } else {
            logger.warn("No matching dialog found to update");
        }
        return scriptDTO;
    }
}
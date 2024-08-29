package com.example.volare.dto;


import com.example.volare.model.Novel;
import com.example.volare.model.Script;
import com.example.volare.model.ScriptScene;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;


public class ScriptDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
     public static class ModifyScriptDTO{

        private List<SceneDTO> scene;

         @Data
         @NoArgsConstructor
         @AllArgsConstructor
         public static class SceneDTO {
             private int scene_num;
             private String location;
             private String time;
             private List<ContentDTO> content;
         }

         @Data
         @NoArgsConstructor
         @AllArgsConstructor
         public static class ContentDTO {
             private String character;
             private String action;
             private String dialog;
             private String type;
         }
     }



     @Getter
     public static class ScriptRequestDTO {
         private List<String> candidates;
         private String text;
     }

// RESPONSE

     @Builder
     @AllArgsConstructor
     @NoArgsConstructor
     @Getter
     public static class NovelToStoryScriptResponseDTO {
         private Script script;

         @JsonInclude(JsonInclude.Include.NON_NULL)
         private String script_str;
         @JsonInclude(JsonInclude.Include.NON_NULL)
         private Long scriptId;


         @Builder
         @AllArgsConstructor
         @NoArgsConstructor
         @Getter
         public static class Script {

             private List<Scene> scene;

             @Builder
             @AllArgsConstructor
             @NoArgsConstructor
             @Getter
             public static class Scene {
                 private List<Content> content;
                 private String location;
                 private int scene_num;
                 private String time;

                 @Builder
                 @AllArgsConstructor
                 @NoArgsConstructor
                 @Getter
                 public static class Content {
                     private String action;
                     private String character;
                     private String dialog;

                     // TODO: type 필요성 확인
                     @JsonInclude(JsonInclude.Include.NON_NULL)
                     private String type;
                 }
             }
         }
     }

     @NoArgsConstructor
     @AllArgsConstructor
     @Getter
     public static class SampleScriptResponseDTO{
         private String sampleScript;
     }



//CONVERTER
     public static NovelToStoryScriptResponseDTO EntityToDTO(Script scriptEntity) {
         return ScriptDTO.NovelToStoryScriptResponseDTO.builder()
                 .script(ScriptDTO.NovelToStoryScriptResponseDTO.Script.builder()
                         .scene(scriptEntity.getScriptScenes().stream()
                                 .map(scene -> ScriptDTO.NovelToStoryScriptResponseDTO.Script.Scene.builder()
                                         .location(scene.getLocation())
                                         .scene_num(scene.getSceneNum())
                                         .time(scene.getTime())
                                         .content(scene.getContents().stream()
                                                 .map(content -> ScriptDTO.NovelToStoryScriptResponseDTO.Script.Scene.Content.builder()
                                                         .action(content.getAction())
                                                         .character(content.getCharacter())
                                                         .dialog(content.getDialog())
                                                         .build()
                                                 )
                                                 .collect(Collectors.toList())
                                         )
                                         .build()
                                 )
                                 .collect(Collectors.toList())
                         )
                         .build()
                 )
                 .scriptId(scriptEntity.getId())
                 .build();
     }

     public static Script convertToEntity(Novel novel, ScriptDTO.NovelToStoryScriptResponseDTO responseDTO, List<String> candidates) {
         Script script = Script.builder()
                 .novel(novel)
                 .characters(candidates)
                 .scriptFile(responseDTO.getScript_str())
                 .scriptScenes(responseDTO.getScript().getScene().stream()
                         .map(sceneDTO -> ScriptScene.builder()
                                 .location(sceneDTO.getLocation())
                                 .sceneNum(sceneDTO.getScene_num())
                                 .time(sceneDTO.getTime())
                                 .contents(sceneDTO.getContent().stream()
                                         .map(content ->  ScriptScene.Content.builder()
                                                 .action(content.getAction())
                                                 .character(content.getCharacter())
                                                 .dialog(content.getDialog())
                                                 .build()
                                         )
                                         .collect(Collectors.toList())
                                 )
                                 .build()
                         )
                         .collect(Collectors.toList())
                 )
                 .build();

         return script;
     }


 }


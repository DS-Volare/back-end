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
             private String props;
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
                 private String props;

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


    @Getter
    @Builder
    public static class ScriptDetailResponseDTO{
        private List<Script> script;

        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        public static class Script {
            private int scene_num;
            private String location;
            private String props;
            private List<Content> content;

            @Builder
            @AllArgsConstructor
            @NoArgsConstructor
            @Getter
            public static class Content {
                private String action;
                private String character;
                private String dialog;
            }
        }
    }



//CONVERTER
     public static NovelToStoryScriptResponseDTO EntityToDTO(Script scriptEntity) {
         return ScriptDTO.NovelToStoryScriptResponseDTO.builder()
                 .script(ScriptDTO.NovelToStoryScriptResponseDTO.Script.builder()
                         .scene(scriptEntity.getScriptScenes().stream()
                                 .map(scene -> NovelToStoryScriptResponseDTO.Script.Scene.builder()
                                         .location(scene.getLocation())
                                         .scene_num(scene.getSceneNum())
                                         .props(scene.getProps())
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
                                 .props(sceneDTO.getProps())
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

    public static ScriptDetailResponseDTO scriptConvertToDto(Script script) {
        // Script 엔티티의 scene 리스트를 변환
        List<ScriptDetailResponseDTO.Script> scripts = script.getScriptScenes().stream()
                .map(scene -> ScriptDetailResponseDTO.Script.builder()
                        .content(scene.getContents().stream() // Script에서 Content 변환
                                .map(content -> ScriptDetailResponseDTO.Script.Content.builder()
                                        .action(content.getAction())   // 엔티티의 action 정보
                                        .character(content.getCharacter()) // 캐릭터 정보
                                        .dialog(content.getDialog())   // 대사 정보
                                        .build())
                                .collect(Collectors.toList()))         // Content 리스트로 변환
                        .location(scene.getLocation())                 // Location 설정
                        .scene_num(scene.getSceneNum())                // Scene 번호 설정
                        .props(scene.getProps())                       // 소품 정보 설정
                        .build())
                .collect(Collectors.toList());

        // 최종적으로 ScriptDetailResponseDTO에 Script 리스트를 포함하여 반환
        return ScriptDetailResponseDTO.builder()
                .script(scripts)
                .build();
    }



}


package com.example.volare.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PACKAGE)
@Table(name = "scripts_scenes")
public class ScriptScene {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "scene_id")
    private Long id;

    //대본 하나당 복수 값
    private String location; // 장소
    private int sceneNum;
    private String props; // 소품

    @ElementCollection
    @CollectionTable(name = "script_scene_contents", joinColumns = @JoinColumn(name = "scene_id"))
    private List<Content> contents; // ContentEntity 객체 리스트

    /* 차후 필요성 확인 후 양방향 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_id")
    private Script script; // Script 외래키

    public void setScript(Script script) {
        this.script = script;
    }
     */

    @Embeddable
    @Getter
    @Builder
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor
    public static class Content{
        private String action; // 비대사
        private String character; //발화자
        private String dialog; // 대사
    }
}

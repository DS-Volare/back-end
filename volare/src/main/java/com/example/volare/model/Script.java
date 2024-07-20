package com.example.volare.model;

import com.example.volare.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PACKAGE)
@Table(name = "story_scripts")
public class Script extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "script_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "novel_id")
    private Novel novel; // 소설 외래키

    private String ScriptFile;

    @ElementCollection
    @CollectionTable(name = "story_script_characters", joinColumns = @JoinColumn(name = "script_id"))
    @Column(name = "character")
    private List<String> characters;

    //TODO: 대본 하나당 단일 값인지 확인
    private String locates; // 장소
    private int sceneNum;
    private String time; // 시간

    @ElementCollection
    @CollectionTable(name = "story_script_contents", joinColumns = @JoinColumn(name = "script_id"))
    private List<Content> contents; // ContentEntity 객체 리스트

    @Embeddable
    @Getter
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor
    public static class Content{
        private String action; // 비대사
        private String character; //발화자
        private String dialog; // 대사
    }
}



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
@Table(name = "scripts")
public class Script extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "script_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "novel_id")
    private Novel novel; // 소설 외래키

    @Column(name = "script_file", columnDefinition = "TEXT", nullable = false)
    private String scriptFile;

    private String type; // 생픔용 type

    @ElementCollection
    @CollectionTable(name = "script_characters", joinColumns = @JoinColumn(name = "script_id"))
    @Column(name = "character")
    private List<String> characters;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "script_id") // 단방향 설정
    private List<ScriptScene> scriptScenes;

    /* 차후 필요성에 따라 양방향 변경
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "script")
    private List<ScriptScene> scriptScenes;
     */
}



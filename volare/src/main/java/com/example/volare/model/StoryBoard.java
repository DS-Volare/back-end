package com.example.volare.model;

import com.example.volare.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PACKAGE)
@Table(name = "story_boards")
public class StoryBoard extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "sb_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "script_id")
    private StoryScript storyScript;

    private String sceneNum;
    private String locate;
    private String time;
    private String summary;
}



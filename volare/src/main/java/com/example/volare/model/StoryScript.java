package com.example.volare.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
public class StoryScript {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "script_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "novel_id")
    private Novel novel;
    private String line;
    private String noLine;
    private String  characters;
    private String locates;
    private String time;
}



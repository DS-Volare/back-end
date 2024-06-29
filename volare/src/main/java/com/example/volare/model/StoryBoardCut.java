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
@Table(name = "story_board_cuts")
public class StoryBoardCut {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "cut_id")
    private Long id;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "sb_id")
    private StoryBoard storyBoard;

    private String cutImage;
    private String cutNum;
    private String angle_cam;


}

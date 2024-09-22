package com.example.volare.model;

import com.example.volare.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PACKAGE)
@Table(name = "novels")
public class Novel extends BaseEntity {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "novel_id")
    private String Id;
    private String title; // 소설 제목

    @Column(name = "story_text", columnDefinition = "TEXT", nullable = false)
    private String storyText;

//    // 스토리보드 이미지 중 첫번째 이미지
//    private String Image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;


    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 목록조회 정렬 기준- 수정시간
    public void updateTimestamp(LocalDateTime time) {
        this.updatedAt = time;
    }

}

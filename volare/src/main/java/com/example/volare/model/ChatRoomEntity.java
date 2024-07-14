package com.example.volare.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="chatroom")
public class ChatRoomEntity {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    // 다대일 관계: 여러 개의 메시지가 하나의 채팅방에 속함
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<MessageEntity> messages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY) // 1
    @JoinColumn(name = "member_id", referencedColumnName = "id") // 2
    private User member;

    @Column(nullable = false)
    private String story_id;

    private String chatsStatus;

    private LocalDate chat_createdAt;
}
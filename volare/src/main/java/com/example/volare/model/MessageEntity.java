package com.example.volare.model;


import com.example.volare.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="message")
public class MessageEntity extends BaseEntity {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    // 다대일 관계: 하나의 채팅방에 속함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatting_id", referencedColumnName = "id")
    private ChatRoomEntity chatRoom;

    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messagetype;

    public enum MessageType {
        QUESTION,GPT
    }

}
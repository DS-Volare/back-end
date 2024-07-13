package com.example.volare.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="message")
public class MessageEntity {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    // 다대일 관계: 하나의 채팅방에 속함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatting_id", referencedColumnName = "id")
    private ChatRoomEntity chatRoom;

    private LocalDate send_time;

    private String message;

}
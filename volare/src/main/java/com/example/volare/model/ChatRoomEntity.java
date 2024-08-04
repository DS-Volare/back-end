package com.example.volare.model;

import com.example.volare.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import static jakarta.persistence.FetchType.LAZY;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="chatroom")
public class ChatRoomEntity extends BaseEntity {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;


    @ManyToOne(fetch = FetchType.LAZY) // 1
    @JoinColumn(name = "user_id", referencedColumnName = "id") // 2
    private User user;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "script_id")
    private Script script;

    private String chatsStatus; // 스토리보드 삭제 시, 채팅도 삭제
}
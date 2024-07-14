package com.example.volare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    @NotNull
    private String email;

    @Column
    private String picture;

    //TODO: Redis 저장으로 변경
    private String accessToken;
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialType socialType;

    @OneToMany(mappedBy = "user")
    private List<ChatRoomEntity> chatRooms = new ArrayList<>();

    public enum SocialType{
        GOOGLE,NAVER
    }

}
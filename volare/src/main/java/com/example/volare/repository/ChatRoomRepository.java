package com.example.volare.repository;

import com.example.volare.model.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity,String> {

    Optional<ChatRoomEntity> findById(String chatRoomId);

}

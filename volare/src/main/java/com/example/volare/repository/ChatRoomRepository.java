package com.example.volare.repository;

import com.example.volare.model.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity,String> {
}

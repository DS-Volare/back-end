package com.example.volare.repository;

import com.example.volare.model.ChatRoomEntity;
import com.example.volare.model.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity,String> {
    // 채팅방 ID로 메세지 전체 조회 - 페이징 처리 필요함
    List<MessageEntity> searchByChatRoom(ChatRoomEntity chatRoom);

}

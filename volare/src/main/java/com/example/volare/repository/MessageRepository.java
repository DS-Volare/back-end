package com.example.volare.repository;

import com.example.volare.model.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<MessageEntity,String> {
    // 채팅방 ID로 메세지 전체 조회 - 페이징 처리 필요함
    List<MessageEntity> findByChatRoomId(String chatRoomId);

}

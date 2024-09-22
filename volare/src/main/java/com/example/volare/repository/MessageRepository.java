package com.example.volare.repository;

import com.example.volare.model.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<MessageEntity,String> {
    /* 페이징 처리 (최신순 -> 과거순 조회)*/
    // 기본적으로 chatRoomId로 메시지를 최신순으로 가져오는 메서드
    Page<MessageEntity> findByChatRoomId(String chatRoomId, Pageable pageable);

    // 특정 메시지 ID 이전의 메시지를 최신순으로 가져오는 메서드
    Page<MessageEntity> findByChatRoomIdAndIdLessThan(String chatRoomId, String lastMessageId, Pageable pageable);

}

package com.example.volare.repository;

import com.example.volare.model.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity,String> {

    Optional<ChatRoomEntity> findById(String chatRoomId);

    @Query("SELECT s.novel.storyText " +
            "FROM ChatRoomEntity c " +
            "JOIN c.script s " +
            "WHERE c.id = :chatRoomId")
    String findStoryTextByChatRoomId( String chatRoomId);

}

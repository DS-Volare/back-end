package com.example.volare.repository;

import com.example.volare.model.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageEntity,String> {
}

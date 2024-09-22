package com.example.volare.repository;

import com.example.volare.model.Novel;
import com.example.volare.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NovelRepository extends JpaRepository<Novel,String> {
    Optional<Novel> findById(String novelId);
    Page<Novel> findByUser(User user, Pageable pageable);
}

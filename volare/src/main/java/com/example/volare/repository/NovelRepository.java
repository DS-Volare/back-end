package com.example.volare.repository;

import com.example.volare.model.Novel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NovelRepository extends JpaRepository<Novel,String> {
    Optional<Novel> findById(String novelId);
}

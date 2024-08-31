package com.example.volare.repository;

import com.example.volare.model.StoryBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryBoardRepository extends JpaRepository<StoryBoard, Long> {
}
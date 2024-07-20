package com.example.volare.repository;

import com.example.volare.model.StoryScript;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoryScriptRepository extends JpaRepository<StoryScript,Long> {
    Optional<StoryScript> findById(Long sbId);
}

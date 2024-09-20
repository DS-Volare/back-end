package com.example.volare.repository;

import com.example.volare.model.Script;
import com.example.volare.model.StoryBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StoryBoardRepository extends JpaRepository<StoryBoard,Long> {
    @Query("SELECT DISTINCT  sb FROM StoryBoard sb JOIN FETCH sb.cuts WHERE sb.script = :script")
    Optional<StoryBoard> findByScript(@Param("script") Script script);

}

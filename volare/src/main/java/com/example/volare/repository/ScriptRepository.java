package com.example.volare.repository;

import com.example.volare.model.Novel;
import com.example.volare.model.Script;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScriptRepository extends JpaRepository<Script,Long> {
    Optional<Script> findById(Long sbId);
    Optional<Script> findByScriptFile(String sampleTag);
    Optional<Script> findByNovel(Novel novel);
}

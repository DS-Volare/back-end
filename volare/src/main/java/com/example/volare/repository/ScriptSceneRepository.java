package com.example.volare.repository;

import com.example.volare.model.ScriptScene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScriptSceneRepository extends JpaRepository<ScriptScene, Long> {
/*
    // 특정 scriptId를 기준으로 ScriptScene을 조회하는 메서드
    List<ScriptScene> findByScriptId(Long scriptId);*/
}
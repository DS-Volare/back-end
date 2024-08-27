package com.example.volare.repository;

import com.example.volare.model.ScriptScene;
import com.example.volare.vo.CharacterStatisticsVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScriptSceneRepository extends JpaRepository<ScriptScene, Long> {
/*
    // 특정 scriptId를 기준으로 ScriptScene을 조회하는 메서드
    List<ScriptScene> findByScriptId(Long scriptId);*/

    // scriptId로 각 대본 별 전체 대사 수 조회
    @Query("SELECT COUNT(c) FROM ScriptScene s " +
            "JOIN s.contents c WHERE s.id = :sceneId")
    long countTotalLinesBySceneId(@Param("sceneId") Long sceneId);


    //scriptId로 각 대본 내 등장인물별 대사 수 조회
    @Query("SELECT new com.example.volare.vo.CharacterStatisticsVO(c.character, COUNT(c)) " +
            "FROM ScriptScene s " +
            "JOIN s.contents c " +
            "WHERE s.id = :sceneId " +
            "GROUP BY c.character")
    List<CharacterStatisticsVO> countLinesByCharacter(@Param("sceneId") Long sceneId);
}
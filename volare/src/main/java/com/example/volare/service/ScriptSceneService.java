package com.example.volare.service;

import com.example.volare.repository.ScriptSceneRepository;
import com.example.volare.vo.CharacterStatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScriptSceneService {

    private final ScriptSceneRepository scriptSceneRepository;

    public Map<String, Double> getCharacterStatistics(Long sceneId) {
        // 전체 대사 수를 계산합니다.
        long totalLines = scriptSceneRepository.countTotalLinesBySceneId(sceneId);

        // 등장인물별 대사 수를 계산합니다.
        List<CharacterStatisticsVO> characterCounts = scriptSceneRepository.countLinesByCharacter(sceneId);

        // 비율을 계산하여 결과를 반환합니다.
        Map<String, Double> statistics = new HashMap<>();
        for (CharacterStatisticsVO vo : characterCounts) {
            String characterName = vo.getCharacterName();
            long count = vo.getCount();
            double percentage = (count * 100.0) / totalLines;
            statistics.put(characterName, percentage);
        }

        return statistics;
    }

}

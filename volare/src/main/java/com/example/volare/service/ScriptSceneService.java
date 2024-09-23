package com.example.volare.service;

import com.example.volare.dto.StatisticsDTO;
import com.example.volare.global.apiPayload.code.status.ErrorStatus;
import com.example.volare.global.apiPayload.exception.handler.GeneralHandler;
import com.example.volare.model.Script;
import com.example.volare.model.ScriptScene;
import com.example.volare.repository.ScriptRepository;
import com.example.volare.repository.ScriptSceneRepository;
import com.example.volare.vo.CharacterStatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScriptSceneService {

    private final ScriptRepository scriptRepository;
    private final ScriptSceneRepository scriptSceneRepository;

    public StatisticsDTO.AppearanceRateDTO getCharacterStatistics(Long scriptId) {

        // 대본 존재 여부 검증
        Script script = scriptRepository.findById(scriptId).orElseThrow(()-> new GeneralHandler(ErrorStatus._BAD_REQUEST));

        // 대본의 모든 ScriptScene PK 조회
        List<Long> sceneIds = script.getScriptScenes().stream()
                .map(ScriptScene::getId)
                .toList();

        // 모든 씬 내 대사 수 계산
        long totalLines = sceneIds.stream().mapToLong(scriptSceneRepository::countTotalLinesBySceneId).sum();

        // 모든 씬 내 등장인물별 대사 수를 계산
        List<CharacterStatisticsVO> allCharacterCounts = sceneIds.stream()
                .flatMap(sceneId -> scriptSceneRepository.countLinesByCharacter(sceneId).stream())
                .toList();

        // 등장인물별 대사 수를 집계
        Map<String, Long> aggregatedCharacterCounts = allCharacterCounts.stream()
                .collect(Collectors.groupingBy(
                        CharacterStatisticsVO::getCharacterName,
                        Collectors.summingLong(CharacterStatisticsVO::getCount)
                ));

        // 비율 계산
        List<StatisticsDTO.AppearanceRateDTO.CharacterStatisticsDTO> characterRate = aggregatedCharacterCounts.entrySet().stream()
                .map(entry -> StatisticsDTO.AppearanceRateDTO.CharacterStatisticsDTO.builder()
                        .characterName(entry.getKey())
                        .percentage((entry.getValue() * 100.0) / totalLines)
                        .build())
                .collect(Collectors.toList());

        // DTO를 반환합니다.
        return StatisticsDTO.AppearanceRateDTO.builder()
                .totalLines(totalLines)
                .characterRate(characterRate)
                .build();
    }

}

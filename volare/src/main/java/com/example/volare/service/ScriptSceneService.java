package com.example.volare.service;

import com.example.volare.dto.AppearanceStatisticsDTO;
import com.example.volare.global.apiPayload.code.status.ErrorStatus;
import com.example.volare.global.apiPayload.exception.handler.GeneralHandler;
import com.example.volare.model.Script;
import com.example.volare.repository.ScriptRepository;
import com.example.volare.repository.ScriptSceneRepository;
import com.example.volare.vo.CharacterStatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScriptSceneService {

    private final ScriptRepository scriptRepository;
    private final ScriptSceneRepository scriptSceneRepository;

    public AppearanceStatisticsDTO getCharacterStatistics(Long scriptId) {

        // 대본 존재 여부 검증
        Script script = scriptRepository.findById(scriptId).orElseThrow(()-> new GeneralHandler(ErrorStatus._BAD_REQUEST));
        Long sceneId = script.getScriptScenes().get(0).getId();

        // 전체 대사 수를 계산
        long totalLines = scriptSceneRepository.countTotalLinesBySceneId(sceneId);

        // 등장인물별 대사 수를 계산
        List<CharacterStatisticsVO> characterCounts = scriptSceneRepository.countLinesByCharacter(sceneId);

        // 비율 계산
        List<AppearanceStatisticsDTO.CharacterStatisticsDTO> characterRate = characterCounts.stream()
                .map(vo -> AppearanceStatisticsDTO.CharacterStatisticsDTO.builder()
                        .characterName(vo.getCharacterName())
                        .percentage((vo.getCount() * 100.0) / totalLines)
                        .build())
                .collect(Collectors.toList());

        // DTO를 반환합니다.
        return AppearanceStatisticsDTO.builder()
                .totalLines(totalLines)
                .characterRate(characterRate)
                .build();
    }

}

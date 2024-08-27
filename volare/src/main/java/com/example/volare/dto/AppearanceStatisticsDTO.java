package com.example.volare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppearanceStatisticsDTO {
    private long totalLines;
    private List<CharacterStatisticsDTO> characterRate;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CharacterStatisticsDTO {
        private String characterName;
        private double percentage;
    }
}
package com.example.volare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class StatisticsDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppearanceRateDTO {
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


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MindMapDTO {
        private String title;
        private List<String> locations;
        private List<String> characters;
    }
}

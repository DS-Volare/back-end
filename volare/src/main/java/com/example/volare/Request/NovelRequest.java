package com.example.volare.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NovelRequest {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
   public static class saveNovelDTO{
       private String title;
       private String novel;
   }
}

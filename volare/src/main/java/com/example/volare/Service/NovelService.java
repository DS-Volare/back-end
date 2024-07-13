package com.example.volare.Service;

import com.example.volare.Request.NovelRequest;
import com.example.volare.model.Novel;
import com.example.volare.repository.NovelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NovelService {

    private final NovelRepository novelRepository;
    private final WebClientService webClientService;

    public void convertNovelToScript(NovelRequest.saveNovelDTO novelDTO){
        Novel novel = Novel.builder()
                .title(novelDTO.getTitle())
                .storyText(novelDTO.getNovel())
                .build();
        novelRepository.save(novel);
        webClientService.ScriptConverter(novel.getStoryText());
    }


}

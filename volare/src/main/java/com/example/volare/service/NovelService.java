package com.example.volare.service;

import com.example.volare.dto.NovelDTO;
import com.example.volare.global.apiPayload.code.status.ErrorStatus;
import com.example.volare.global.apiPayload.exception.handler.GeneralHandler;
import com.example.volare.model.Novel;
import com.example.volare.model.User;
import com.example.volare.repository.NovelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NovelService {

    private final NovelRepository novelRepository;

    // 원본 소설 저장
    @Transactional
    public Long saveNovel(User user, NovelDTO.NovelRequestDTO novel){

        Novel saveNovel = Novel.builder()
                .title(novel.getTitle())
                .storyText(novel.getNovel())
                .user(user)
                .updatedAt(LocalDateTime.now())
                .build();
        Novel save = novelRepository.save(saveNovel);

        return save.getId();
    }

    // 소설정보 조회
    public NovelDTO.NovelDetailResponseDTO getNovelDetail(User user,Long novelId){
        Novel novel = novelRepository.findById(novelId).orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));
        return NovelDTO.NovelDetailResponseDTO.builder()
                .title(novel.getTitle())
                .storyText(novel.getStoryText())
                .build();
    }


}

package com.example.volare.service;

import com.example.volare.dto.NovelDTO;
import com.example.volare.model.Novel;
import com.example.volare.model.User;
import com.example.volare.repository.NovelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NovelService {

    private final NovelRepository novelRepository;

    // 원본 소설 저장
    @Transactional
    public String saveNovel(User user, NovelDTO.NovelRequestDTO novel){

        Novel saveNovel = Novel.builder()
                .title(novel.getTitle())
                .storyText(novel.getNovel())
                .user(user)
                .build();

        Novel save = novelRepository.save(saveNovel);
        return save.getId();
    }

    // 유저별 소설 변환 내역 조회
    public List<NovelDTO.NovelCovertListDTO>  getConvertList(User user, int pageNo){
        //TODO: 단계 중 일부만 진행했을때 - 사진 이미지 반환 여부 확인(기본 이미지 or No이미지)
        pageNo = (pageNo == 0) ? 0 : (pageNo - 1);
        Pageable pageable = PageRequest.of(pageNo, 6, Sort.by(Sort.Order.asc("title"),Sort.Order.desc("createdAt")));
        Page<Novel> novelList = novelRepository.findByUser(user,pageable);
        List<NovelDTO.NovelCovertListDTO> listDTO = novelList.stream()
                .map(list -> NovelDTO.NovelCovertListDTO
                        .builder()
                        .title(list.getTitle())
                        .build())
                .toList();
        return listDTO ;
    }

}

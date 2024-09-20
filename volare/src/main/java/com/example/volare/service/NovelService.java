package com.example.volare.service;

import com.example.volare.dto.NovelDTO;
import com.example.volare.model.Novel;
import com.example.volare.model.Script;
import com.example.volare.model.StoryBoardCut;
import com.example.volare.model.User;
import com.example.volare.repository.NovelRepository;
import com.example.volare.repository.ScriptRepository;
import com.example.volare.repository.StoryBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NovelService {

    private final NovelRepository novelRepository;
    private final StoryBoardRepository storyBoardRepository;
    private final ScriptRepository scriptRepository;

    private final static  String falLImgURL = "로고url";

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
    //TODO: 정렬기준: 채팅 내역> 스토리보드 생성> 대본 생성 수정순서 기준
    public NovelDTO.UserConvertDTO getConvertList(User user, int pageNo) {
        // pageNo가 0이면 첫 페이지(0번째 페이지)를 반환, 아니면 입력한 페이지 - 1을 사용
        pageNo = (pageNo == 0) ? 0 : (pageNo - 1);

        // 정렬 기준: title 오름차순, createdAt 내림차순
        Pageable pageable = PageRequest.of(pageNo, 6, Sort.by(Sort.Order.asc("title"), Sort.Order.desc("createdAt")));

        // 유저 기준으로 소설 리스트를 페이징하여 조회
        Page<Novel> novelList = novelRepository.findByUser(user, pageable);

        // Novel -> NovelCovertListDTO로 변환
        List<NovelDTO.NovelConvertListDTO> listDTO = novelList.stream()
                .map(this::toNovelConvertListDTO)
                .toList();

        return NovelDTO.conversionConvert(novelList,listDTO);
    }



    private NovelDTO.NovelConvertListDTO toNovelConvertListDTO(Novel novel) {

        Optional<Script> scriptOptional = scriptRepository.findByNovel(novel);

        String storyboardUrl = scriptOptional
                .flatMap(storyBoardRepository::findByScript)
                .flatMap(storyBoard -> storyBoard.getCuts().stream()
                        .findFirst())  // 첫 번째 StoryBoardCut 찾기
                .map(StoryBoardCut::getCutImage)  // StoryBoardCut에서 cutImage 가져오기
                .orElse(falLImgURL);  // 기본 URL 반환

        return NovelDTO.NovelConvertListDTO.builder()
                .title(novel.getTitle())
                .image(storyboardUrl)
                .build();
    }

}

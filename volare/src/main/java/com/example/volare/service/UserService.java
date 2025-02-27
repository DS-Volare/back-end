package com.example.volare.service;

import com.example.volare.dto.*;
import com.example.volare.global.apiPayload.code.status.ErrorStatus;
import com.example.volare.global.apiPayload.exception.GeneralException;
import com.example.volare.global.common.auth.AuthRedisService;
import com.example.volare.global.common.auth.JwtService;
import com.example.volare.model.*;
import com.example.volare.repository.ChatRoomRepository;
import com.example.volare.repository.NovelRepository;
import com.example.volare.repository.ScriptRepository;
import com.example.volare.repository.StoryBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthRedisService authRedisService;
    private final JwtService jwtService;


    private final StoryBoardRepository storyBoardRepository;
    private final ScriptRepository scriptRepository;
    private final NovelRepository novelRepository;

    private final static  String falLImgURL = null;

    private final NovelService novelService;
    private final ScriptService scriptService;
    private final StoryboardService storyboardService;
    private final ChatRoomRepository chatRoomRepository;

    public void signOut(String accessToken, String refreshToken) {
        long expiredAccessTokenTime = jwtService.getClaims((accessToken)).getExpiration().getTime() - new Date().getTime();
        authRedisService.setValuesWithTimeout("blackList" + accessToken, accessToken,expiredAccessTokenTime);
        authRedisService.deleteValues(refreshToken);
    }

    public UserDTO getUserInfo(User user){
        return UserDTO.builder()
                .email(user.getEmail())
                .build();
    }

    // 유저별 소설 변환 내역 조회
    public NovelDTO.UserConvertDTO getConvertList(User user, int pageNo) {
        // pageNo가 0이면 첫 페이지(0번째 페이지)를 반환, 아니면 입력한 페이지 - 1을 사용
        pageNo = (pageNo == 0) ? 0 : (pageNo - 1);

        Pageable pageable = PageRequest.of(pageNo, 6,
                Sort.by(
                        Sort.Order.desc("updatedAt"),   // 1단계: 채팅,대본,스토리보드에 대한 수정 시간 내림차순
                        Sort.Order.asc("title")         // 2단계: 제목 오름차순
                )
        );

        // 유저 기준으로 소설 리스트를 페이징하여 조회
        Page<Novel> novelList = novelRepository.findByUser(user, pageable);

        // Novel -> NovelCovertListDTO로 변환
        List<NovelDTO.NovelConvertListDTO> listDTO = novelList.stream()
                .map(this::toNovelConvertListDTO)
                .toList();

        return NovelDTO.conversionConvert(novelList,listDTO);
    }


    private NovelDTO.NovelConvertListDTO toNovelConvertListDTO(Novel novel) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Optional<Script> scriptOptional = scriptRepository.findByNovel(novel);

        String storyboardUrl = scriptOptional
                .flatMap(storyBoardRepository::findByScript)
                .flatMap(storyBoard -> storyBoard.getCuts().stream()
                        .findFirst())  // 첫 번째 StoryBoardCut 찾기
                .map(StoryBoardCut::getCutImage)  // StoryBoardCut에서 cutImage 가져오기
                .orElse(falLImgURL);  // 기본 URL 반환

        return NovelDTO.NovelConvertListDTO.builder()
                .novelId(novel.getId())
                .title(novel.getTitle())
                .image(storyboardUrl)
                .updatedAt(novel.getUpdatedAt().format(formatter))
                .build();
    }

    public ConvertDetailDTO getUserConvertDetail(User user, String  novelId) {

        // 소설 조회
        Novel novel = novelRepository.findById(novelId).orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));
        NovelDTO.NovelDetailResponseDTO novelDetail = novelService.getNovelDetail(user, novelId);


        // 스크립트 조회
        Script script = scriptRepository.findByNovel(novel).orElse(null);
        ConvertWrapper<ScriptDTO.ScriptDetailResponseDTO> scriptDetailWrapper = Optional.ofNullable(script)
                .map(s -> new ConvertWrapper<>(scriptService.getScriptDetail(s.getId())))
                .orElse(new ConvertWrapper<>(false,"사용자가 과거의 생성한 대본 내역이 없습니다."));

        // 채팅방 ID 조회
        ChatRoomEntity chatRoom = chatRoomRepository.findChatRoomEntityByScript(script).orElse(null);

        //스토리보드 조회
        StoryBoard storyBoard = storyBoardRepository.findByScript(script).orElse(null);
        ConvertWrapper<StoryboardDTO.Response> storyBoardDetailWrapper = Optional.ofNullable(storyBoard)
                .map(sb -> new ConvertWrapper<>(storyboardService.getStoryBoardDetail(sb.getId())))
                .orElse(new ConvertWrapper<>(false,"사용자가 과거의 생성한 스토리보드 내역이 없습니다."));

        return ConvertDetailDTO.fromDTO(novelDetail,scriptDetailWrapper,chatRoom,storyBoardDetailWrapper);

    }


}

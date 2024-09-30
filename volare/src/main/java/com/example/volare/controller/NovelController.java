package com.example.volare.controller;

import com.example.volare.dto.NovelDTO;
import com.example.volare.global.apiPayload.ApiResponse;
import com.example.volare.global.common.auth.model.AuthUser;
import com.example.volare.service.NovelService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("novels")
@RequiredArgsConstructor
public class NovelController {

    private final NovelService novelService;

    // 소설 저장
    @PostMapping("")
    public ApiResponse<NovelDTO.NovelResponseDTO> saveNovel(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody NovelDTO.NovelRequestDTO req )
    {
        String saveNovel = novelService.saveNovel(authUser.getUser(), req);
        return ApiResponse.onSuccess(new NovelDTO.NovelResponseDTO(saveNovel));
    }

    // 유저별 소설 변환 내역 조회
    @GetMapping("")
    public ApiResponse<NovelDTO.UserConvertDTO> getMyConvertList(
            @AuthenticationPrincipal AuthUser authUser, @RequestParam(required = false, defaultValue = "0", value = "pageNo") int pageNo)
    {
        NovelDTO.UserConvertDTO convertList = novelService.getConvertList(authUser.getUser(),pageNo);
        return ApiResponse.onSuccess(convertList);
    }
}

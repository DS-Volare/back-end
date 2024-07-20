package com.example.volare.controller;

import com.example.volare.dto.NovelDTO;
import com.example.volare.global.apiPayload.ApiResponse;
import com.example.volare.global.common.auth.model.AuthUser;
import com.example.volare.service.NovelService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("novels")
@RequiredArgsConstructor
public class NovelController {

    private final NovelService novelService;

    // TODO: 단일책임원칙- 소설 저장과 소설 변환 로직 분리 제안
    // 소설 저장
    @PostMapping("")
    public ApiResponse<NovelDTO.NovelResponseDTO> saveNovel(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody NovelDTO.NovelRequestDTO req )
    {
        String saveNovel = novelService.saveNovel(authUser.getUser(), req);
        return ApiResponse.onSuccess(new NovelDTO.NovelResponseDTO(saveNovel));
    }
}

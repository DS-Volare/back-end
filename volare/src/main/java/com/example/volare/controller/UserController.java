package com.example.volare.controller;

import com.example.volare.dto.NovelDTO;
import com.example.volare.dto.UserDTO;
import com.example.volare.global.apiPayload.ApiResponse;
import com.example.volare.global.apiPayload.code.status.ErrorStatus;
import com.example.volare.global.apiPayload.exception.handler.GeneralHandler;
import com.example.volare.global.common.auth.JwtService;
import com.example.volare.global.common.auth.model.AuthUser;
import com.example.volare.global.common.auth.model.TokenDTO;
import com.example.volare.service.NovelService;
import com.example.volare.service.ScriptService;
import com.example.volare.service.StoryboardService;
import com.example.volare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final JwtService jwtService;
    private final UserService userService;

    private final NovelService novelService;
    private final ScriptService scriptService;
    private final StoryboardService storyboardService;

    // 로그아웃
    @PostMapping("/sign-out")
    public ApiResponse<?> signOut(@RequestHeader("X-AUTH-TOKEN") String accessToken,
                                  @RequestHeader("refresh-Token") String refreshToken) {
        userService.signOut(accessToken, refreshToken);
        return ApiResponse.onSuccess("로그아웃");
    }

    // 토큰 재발급
    @PostMapping("/reissue-token")
    public ResponseEntity reissueToken(@RequestHeader("X-AUTH-TOKEN") String accessToken,
                                       @RequestHeader("refresh-Token") String refreshToken) {
        TokenDTO tokens = jwtService.reissueToken(accessToken, refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add("accessToken", tokens.getAccessToken());
        headers.add("refreshToken", tokens.getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(headers)
                .build();
    }

    // 사용자 정보 조회
    @GetMapping()
    public ApiResponse<UserDTO> retrieveUserInfo(@AuthenticationPrincipal AuthUser authUser){
        return ApiResponse.onSuccess(userService.getUserInfo(authUser.getUser()));
    }

    // 유저별 소설 변환 내역 조회
    @GetMapping("/conversion")
    public ApiResponse<NovelDTO.UserConvertDTO> getMyConvertList
    (
            @AuthenticationPrincipal AuthUser authUser, @RequestParam(required = false, defaultValue = "0", value = "pageNo") int pageNo
    ) {
        NovelDTO.UserConvertDTO convertList = userService.getConvertList(authUser.getUser(),pageNo);
        return ApiResponse.onSuccess(convertList);
    }


    // 변환 내역 상세 조회
    @GetMapping("/conversion-details/{id}")
    public ApiResponse<?> getConvertDetail( @AuthenticationPrincipal AuthUser authUser,@PathVariable Long id, @RequestParam String type) {
        switch (type) {
            case "N":
                // 소설 정보조회 대한 처리
                return ApiResponse.onSuccess(novelService.getNovelDetail(authUser.getUser(),id));

            case "S":
                // 대본 정보조회 대한 처리
                return ApiResponse.onSuccess(scriptService.getScriptDetail(id));

            case "SB":
                // 스토리보드 정보조회 대한 처리
                return ApiResponse.onSuccess(storyboardService.getStoryBoardDetail(id));
            default:
                throw new GeneralHandler(ErrorStatus._BAD_REQUEST);
        }
    }


}

package com.example.volare.Controller;

import com.example.volare.Request.NovelRequest;
import com.example.volare.Service.NovelService;
import com.example.volare.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("novels")
public class NovelController {
    private final NovelService novelService;

    // 소설 변환
    @PostMapping()
    public ApiResponse convertNovelToStory(@RequestBody NovelRequest.saveNovelDTO novelInfo){
        novelService.convertNovelToScript(novelInfo);
        return ApiResponse.onSuccess(null);
    }

    // 내 작업 소설 정보 보기
}

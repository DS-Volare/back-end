package com.example.volare.controller;

import com.example.volare.dto.StoryboardDTO;
import com.example.volare.service.StoryboardService;
import com.example.volare.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sb")
@RequiredArgsConstructor
public class StoryboardController {

    private final StoryboardService storyboardService;

    @PostMapping("/generate-storyboard")
    public ApiResponse<StoryboardDTO.Response> generateStoryboard(@RequestBody StoryboardDTO.Request request) {
        StoryboardDTO.Response response = storyboardService.generateStoryboard(request);
        return ApiResponse.onSuccess(response);
    }
}
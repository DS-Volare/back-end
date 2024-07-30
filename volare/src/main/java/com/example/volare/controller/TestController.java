package com.example.volare.controller;

import com.example.volare.global.apiPayload.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/testFE")
    public ApiResponse<String> sayHello() {
        return ApiResponse.onSuccess("cors test");
    }
}
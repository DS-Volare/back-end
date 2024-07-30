package com.example.volare.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/testyou")
    public String sayHello() {
        return "For 유수연";
    }
}
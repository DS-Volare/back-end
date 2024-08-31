package com.example.volare.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ScriptResponseDTO {
    private String title;
    private List<String> locations;
    private List<String> characters;
}
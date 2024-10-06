package com.example.volare.dto;

import lombok.Getter;

@Getter
public class ConvertWrapper<T> {

    private T data;
    private Boolean isExist;
    private String message;

    public ConvertWrapper(T data) {
        this.data = data;
        this.isExist = true;
        this.message = "사용자의 과거 변환 내역이 존재합니다.";
    }

    public ConvertWrapper(Boolean isExist, String message) {
        this.data = null;
        this.isExist = isExist;
        this.message = message;
    }

}

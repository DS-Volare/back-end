package com.example.volare.global.apiPayload.exception.handler;

import com.example.volare.global.apiPayload.code.BaseErrorCode;
import com.example.volare.global.apiPayload.exception.GeneralException;

public class GeneralHandler extends GeneralException {
    public GeneralHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
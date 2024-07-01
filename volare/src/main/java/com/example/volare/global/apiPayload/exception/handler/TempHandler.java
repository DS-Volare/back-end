package com.example.volare.global.apiPayload.exception.handler;

import com.example.volare.global.apiPayload.code.BaseErrorCode;
import com.example.volare.global.apiPayload.exception.GeneralException;

public class TempHandler extends GeneralException {
    public TempHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
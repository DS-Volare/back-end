package com.example.volare.global.apiPayload.code.status;

import com.example.volare.global.apiPayload.code.BaseErrorCode;
import com.example.volare.global.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // token 관련 에러 +1
    _INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN400", "유효하지 않은 토큰입니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.FORBIDDEN, "TOKEN403", "X-AUTH-TOKEN이 만료되었습니다. 토큰 재발급을 실행해주세요."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "TOKEN404", "refresh-token이 만료되었습니다. 재로그인이 필요합니다."),


    // email +2
    EMAIL_NOT_SEND(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL500", "이메일 전송에 실패하였습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
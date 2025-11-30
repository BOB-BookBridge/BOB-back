package com.bob.global.exception.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthenticationError {
    FAILED_AUTHENTICATION(HttpStatus.UNAUTHORIZED, "E001", "인증에 실패하였습니다."),
    IS_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "E002", "인증 정보가 만료되었습니다."),
    FAILED_GET_AUTHENTICATION_INFORMATION(HttpStatus.UNAUTHORIZED, "E003", "인증 정보를 확인할 수 없습니다. 다시 로그인 해주세요."),
    IS_DEACTIVATED_MEMBER(HttpStatus.UNAUTHORIZED, "E004", "탈퇴한 계정입니다."),
    IS_BANNED_MEMBER(HttpStatus.UNAUTHORIZED, "E005", "제재된 계정입니다."),
    LOGIN_RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "E006", "로그인 시도 횟수를 초과했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}


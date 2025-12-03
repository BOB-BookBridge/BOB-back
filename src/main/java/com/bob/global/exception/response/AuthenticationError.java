package com.bob.global.exception.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthenticationError {
    AUTHENTICATION_FAILED("인증에 실패하였습니다.", HttpStatus.UNAUTHORIZED),

    ACCESS_TOKEN_EXPIRED("인증 토큰이 만료되었습니다.", HttpStatus.GONE),

    MEMBER_DEACTIVATED("탈퇴한 계정입니다.", HttpStatus.FORBIDDEN),
    MEMBER_BANNED("제재된 계정입니다.", HttpStatus.FORBIDDEN),

    LOGIN_RATE_LIMIT_EXCEEDED("로그인 시도 횟수를 초과했습니다.", HttpStatus.TOO_MANY_REQUESTS);

    private final String message;
    private final HttpStatus status;
}


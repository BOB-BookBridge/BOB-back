package com.bob.global.exception.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthenticationError {
  FAILED_AUTHENTICATION("E001", "인증에 실패하였습니다."),
  IS_EXPIRED_TOKEN("E002", "만료된 토큰입니다.");

  private String code;
  private String message;
}


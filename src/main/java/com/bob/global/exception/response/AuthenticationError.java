package com.bob.global.exception.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthenticationError {
  FAILED_AUTHENTICATION("E001", "인증에 실패하였습니다."),
  IS_EXPIRED_TOKEN("E002", "인증 정보가 만료되었습니다."),
  FAILED_GET_AUTHENTICATION_INFORMATION("E003", "인증 정보를 확인할 수 없습니다. 다시 로그인 해주세요."),
  IS_REMOVED_MEMBER("E004", "탈퇴한 계정입니다.");

  private String code;
  private String message;
}


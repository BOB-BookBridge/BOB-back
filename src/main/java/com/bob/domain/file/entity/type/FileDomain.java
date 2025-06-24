package com.bob.domain.file.entity.type;

import static com.bob.global.exception.response.ApplicationError.UN_SUPPORTED_TYPE;

import com.bob.global.exception.exceptions.ApplicationException;

public enum FileDomain {
  POST,
  CHAT;

  public static FileDomain from(String value) {
    try {
      return FileDomain.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new ApplicationException(UN_SUPPORTED_TYPE);
    }
  }
}
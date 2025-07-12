package com.bob.domain.post.entity.status;

import static com.bob.global.exception.response.ApplicationError.UN_SUPPORTED_BOOK_STATUS;

import com.bob.global.exception.exceptions.ApplicationException;
import java.util.Arrays;

public enum BookStatus {
  BEST, HIGH, MEDIUM, LOW;

  public static BookStatus from(String status) {
    return Arrays.stream(BookStatus.values())
        .filter(value -> value.name().equalsIgnoreCase(status))
        .findFirst()
        .orElseThrow(() -> new ApplicationException(UN_SUPPORTED_BOOK_STATUS));
  }
}

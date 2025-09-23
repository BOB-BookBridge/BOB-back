package com.bob.domain.book.service.dto.query;

public record SearchBookQuery(
    String key,
    String keyword
) {

  public static SearchBookQuery of(String key, String keyword) {
    return new SearchBookQuery(key, keyword);
  }
}

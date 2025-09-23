package com.bob.domain.book.service.dto.query;

public record ReadBookDetailQuery(
    Long id
) {

  public static ReadBookDetailQuery of(Long id) {
    return new ReadBookDetailQuery(id);
  }
}

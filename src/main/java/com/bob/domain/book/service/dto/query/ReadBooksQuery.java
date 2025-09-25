package com.bob.domain.book.service.dto.query;

import java.util.List;

public record ReadBooksQuery(
    List<Long> ids
) {

  public static ReadBooksQuery of(List<Long> ids) {
    return new ReadBooksQuery(ids);
  }
}

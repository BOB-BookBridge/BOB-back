package com.bob.domain.member.service.port.out;

import com.bob.domain.book.service.dto.response.BookResponse;
import java.time.LocalDate;
import java.util.List;

public interface MemberBookPort {

  Long create(String isbn13, String title, String author, String description, Integer priceStandard, String cover,
      LocalDate pubDate);

  // TODO: port dto (BookView) 전환, 변환은 adapter에서 수행
  List<BookResponse> readBookSummaries(List<Long> ids);

  BookResponse readBookSummary(Long id);
}

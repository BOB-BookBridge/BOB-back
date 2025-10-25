package com.bob.domain.member.service.port.out;

import com.bob.domain.book.service.dto.response.BookResponse;
import java.time.LocalDate;
import java.util.List;

public interface MemberBookPort {

  Long create(String isbn13, String title, String author, String description, Integer priceStandard, String cover,
      LocalDate pubDate);

  List<BookResponse> readBookSummaries(List<Long> ids);

  BookResponse readBookSummary(Long id);
}

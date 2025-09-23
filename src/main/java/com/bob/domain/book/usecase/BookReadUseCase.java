package com.bob.domain.book.usecase;

import com.bob.domain.book.service.dto.query.ReadBookDetailQuery;
import com.bob.domain.book.service.dto.query.SearchBookQuery;
import com.bob.domain.book.service.dto.response.BookResponse;
import java.util.List;

public interface BookReadUseCase {

  BookResponse readBookProcess(ReadBookDetailQuery query);

  List<Long> searchBookIdsProcess(SearchBookQuery query);
}

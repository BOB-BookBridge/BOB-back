package com.bob.domain.member.service.port.out;

import com.bob.domain.book.service.dto.command.CreateBookCommand;
import com.bob.domain.book.service.dto.response.BookResponse;
import java.util.List;

public interface MemberBookPort {

  Long createBook(CreateBookCommand command);

  List<BookResponse> readBookSummaries(List<Long> ids);
}

package com.bob.domain.member.service.port.out;

import com.bob.domain.book.service.dto.command.CreateBookCommand;

public interface MemberBookPort {

  Long createBook(CreateBookCommand command);
}

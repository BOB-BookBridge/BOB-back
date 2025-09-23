package com.bob.domain.book.usecase;

import com.bob.domain.book.service.dto.command.CreateBookCommand;

public interface BookWriteUseCase {

  Long createBookProcess(CreateBookCommand command);
}

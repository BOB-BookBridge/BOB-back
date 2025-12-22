package com.bob.core.book.application.port.in;

import com.bob.core.book.application.dto.command.RegisterBookCommand;
import com.bob.core.book.domain.Book;

public interface BookRegister {

    Book register(RegisterBookCommand command);
}

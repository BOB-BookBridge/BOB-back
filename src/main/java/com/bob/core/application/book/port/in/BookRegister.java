package com.bob.core.application.book.port.in;

import com.bob.core.application.book.dto.command.RegisterBookCommand;
import com.bob.core.domain.book.Book;

public interface BookRegister {

    Book register(RegisterBookCommand command);
}

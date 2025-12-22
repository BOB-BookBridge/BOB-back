package com.bob.core.bookcase.application.port.in;

import com.bob.core.bookcase.application.dto.command.RegisterBookcaseItemCommand;
import com.bob.core.bookcase.domain.BookcaseItem;

public interface BookcaseRegister {

    BookcaseItem registerItem(RegisterBookcaseItemCommand command);
}

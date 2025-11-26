package com.bob.core.application.bookcase.port.in;

import com.bob.core.application.bookcase.dto.command.RegisterBookcaseItemCommand;
import com.bob.core.domain.bookcase.BookcaseItem;

public interface BookcaseRegister {

    BookcaseItem registerItem(RegisterBookcaseItemCommand command);
}

package com.bob.core.application.bookcase.port.in;

import java.util.List;

import com.bob.core.application.bookcase.dto.command.DeleteBookcaseItemCommand;

public interface BookcaseDeleter {

    void deleteItems(List<Long> ids);

    void delete(Long id, DeleteBookcaseItemCommand command);
}

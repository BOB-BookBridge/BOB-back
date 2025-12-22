package com.bob.core.bookcase.application.port.in;

import java.util.List;

import com.bob.core.bookcase.application.dto.command.DeleteBookcaseItemCommand;

public interface BookcaseDeleter {

    void deleteItems(List<Long> ids);

    void delete(Long id, DeleteBookcaseItemCommand command);
}

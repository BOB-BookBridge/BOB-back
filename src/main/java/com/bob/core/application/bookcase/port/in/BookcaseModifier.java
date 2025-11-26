package com.bob.core.application.bookcase.port.in;

import java.util.List;

import com.bob.core.application.bookcase.dto.command.AllocateUsageCommand;
import com.bob.core.application.bookcase.dto.command.FreeUsageByRefIdCommand;
import com.bob.core.application.bookcase.dto.command.FreeUsageCommand;

public interface BookcaseModifier {

    void allocate(List<Long> ids, AllocateUsageCommand command);

    void free(List<Long> ids, FreeUsageCommand command);

    void freeByRefId(FreeUsageByRefIdCommand command);
}

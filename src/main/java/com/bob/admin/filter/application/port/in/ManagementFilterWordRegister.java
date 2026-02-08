package com.bob.admin.filter.application.port.in;

import com.bob.admin.filter.application.dto.command.CreateFilterWordCommand;
import com.bob.admin.filter.domain.ManagementFilterWord;

public interface ManagementFilterWordRegister {

    ManagementFilterWord register(CreateFilterWordCommand command);
}

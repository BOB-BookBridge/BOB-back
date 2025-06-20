package com.bob.domain.area.usecase;

import com.bob.domain.area.service.dto.command.AuthenticationCommand;

public interface AreaModifyUseCase {

  void authenticateProcess(AuthenticationCommand command);
}

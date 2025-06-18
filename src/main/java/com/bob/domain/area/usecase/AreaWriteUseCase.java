package com.bob.domain.area.usecase;

import com.bob.domain.area.service.dto.command.CreateAreaCommand;

public interface AreaWriteUseCase {

  void createActivityAreaProcess(CreateAreaCommand command);
}

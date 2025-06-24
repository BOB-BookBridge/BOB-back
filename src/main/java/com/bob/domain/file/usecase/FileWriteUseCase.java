package com.bob.domain.file.usecase;

import com.bob.domain.file.service.dto.command.RegisterFileCommand;

public interface FileWriteUseCase {

  void registerFileProcess(RegisterFileCommand command);
}

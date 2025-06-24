package com.bob.domain.file.usecase;

import com.bob.domain.file.service.dto.command.ModifyReferenceIdCommand;

public interface FileModifyUseCase {

  void modifyReferenceIdProcess(ModifyReferenceIdCommand command);
}

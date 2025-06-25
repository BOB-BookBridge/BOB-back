package com.bob.domain.file.usecase;

import com.bob.domain.file.service.dto.command.ChangeFileCommand;
import com.bob.domain.file.service.dto.command.ModifyReferenceIdCommand;

public interface FileModifyUseCase {

  void changeFileProcess(ChangeFileCommand command);

  void modifyReferenceIdProcess(ModifyReferenceIdCommand command);
}

package com.bob.domain.file.usecase;

import com.bob.domain.file.service.dto.command.RegisterFileCommand;
import com.bob.domain.file.service.dto.command.GenerateFileUploadUrlCommand;
import com.bob.domain.file.service.dto.response.FileUploadUrlResponse;

public interface FileWriteUseCase {

  FileUploadUrlResponse generateFileUploadUrl(GenerateFileUploadUrlCommand command);

  void registerFileProcess(RegisterFileCommand command);
}

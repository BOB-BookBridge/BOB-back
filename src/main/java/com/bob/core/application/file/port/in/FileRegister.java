package com.bob.core.application.file.port.in;

import java.util.List;

import com.bob.core.application.file.dto.command.GenerateFileUploadUrlCommand;
import com.bob.core.application.file.dto.command.RegisterFilesCommand;
import com.bob.core.application.file.dto.result.FileUploadUrl;
import com.bob.core.domain.file.File;

public interface FileRegister {

    List<FileUploadUrl> generateFileUploadUrl(GenerateFileUploadUrlCommand command);

    List<File> registerFiles(RegisterFilesCommand command);
}

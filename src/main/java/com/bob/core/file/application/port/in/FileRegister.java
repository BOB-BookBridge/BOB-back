package com.bob.core.file.application.port.in;

import java.util.List;

import com.bob.core.file.application.dto.command.GenerateFileUploadUrlCommand;
import com.bob.core.file.application.dto.command.RegisterFilesCommand;
import com.bob.core.file.application.dto.result.FileUploadUrl;
import com.bob.core.file.domain.File;

public interface FileRegister {

    List<FileUploadUrl> generateFileUploadUrl(GenerateFileUploadUrlCommand command);

    List<File> registerFiles(RegisterFilesCommand command);
}

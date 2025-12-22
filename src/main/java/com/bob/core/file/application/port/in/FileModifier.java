package com.bob.core.file.application.port.in;

import java.util.List;

import com.bob.core.file.application.dto.command.MappingFileReferencesCommand;
import com.bob.core.file.application.dto.command.UpdateFilesCommand;
import com.bob.core.file.domain.File;

public interface FileModifier {

    List<File> updateFiles(UpdateFilesCommand command);

    void mappingReferences(MappingFileReferencesCommand command);
}

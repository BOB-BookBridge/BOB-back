package com.bob.core.application.file.port.in;

import java.util.List;

import com.bob.core.application.file.dto.command.MappingFileReferencesCommand;
import com.bob.core.application.file.dto.command.UpdateFilesCommand;
import com.bob.core.domain.file.File;

public interface FileModifier {

    List<File> updateFiles(UpdateFilesCommand command);

    void mappingReferences(MappingFileReferencesCommand command);
}

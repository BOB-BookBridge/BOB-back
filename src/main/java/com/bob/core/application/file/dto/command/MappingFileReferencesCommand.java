package com.bob.core.application.file.dto.command;

import java.util.List;

public record MappingFileReferencesCommand(List<String> names, String referenceId) {

}

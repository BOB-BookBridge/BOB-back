package com.bob.core.file.application.dto.command;

import java.util.List;

public record MappingFileReferencesCommand(List<String> names, String referenceId) {

}

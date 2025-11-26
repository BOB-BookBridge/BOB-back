package com.bob.core.application.file.dto.command;

import java.util.List;

public record GenerateFileUploadUrlCommand(String domain, List<String> contentTypes) {

}

package com.bob.core.file.application.dto.command;

import java.util.List;

public record GenerateFileUploadUrlCommand(String domain, List<String> contentTypes) {

}

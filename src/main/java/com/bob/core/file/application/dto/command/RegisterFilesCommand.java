package com.bob.core.file.application.dto.command;

import java.util.List;
import java.util.UUID;

public record RegisterFilesCommand(String domain, List<String> fileNames, UUID memberId) {

}

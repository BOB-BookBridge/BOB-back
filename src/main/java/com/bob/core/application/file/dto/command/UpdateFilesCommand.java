package com.bob.core.application.file.dto.command;

import java.util.List;
import java.util.UUID;

public record UpdateFilesCommand(String domain, String referenceId, List<String> names, UUID memberId) {

}

package com.bob.domain.file.service.dto.command;

import java.util.List;

public record ModifyReferenceIdCommand(
    List<String> fileNames,
    String domainId
) {

}

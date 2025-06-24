package com.bob.domain.file.service.dto.command;

public record ModifyReferenceIdCommand(
    String oldReferenceId,
    Long currentReferenceId
) {

}

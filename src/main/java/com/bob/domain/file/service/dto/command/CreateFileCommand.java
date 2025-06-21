package com.bob.domain.file.service.dto.command;

import com.bob.domain.file.entity.File;
import com.bob.domain.file.entity.type.FileDomain;

public record CreateFileCommand(
    FileDomain domain,
    Object referenceId,
    String fileName,
    int sequence
) {

  public File toEntity() {
    return File.builder()
        .domain(domain)
        .referenceId(referenceId.toString())
        .fileName(fileName)
        .sequence(sequence)
        .build();
  }
}
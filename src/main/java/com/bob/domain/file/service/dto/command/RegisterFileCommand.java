package com.bob.domain.file.service.dto.command;

import static com.bob.domain.file.entity.type.FileDomain.from;

import com.bob.domain.file.entity.File;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record RegisterFileCommand(
    String domain,
    List<String> fileNames,
    UUID memberId
) {

  public List<File> toEntities() {
    return fileNames.stream().map(fileName -> File.builder()
            .fileName(fileName)
            .domain(from(domain))
            .referenceId(null)
            .sequence(null)
            .uploader(memberId)
            .build())
        .toList();
  }
}
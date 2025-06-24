package com.bob.domain.file.service.dto.command;

import static java.util.stream.IntStream.range;

import com.bob.domain.file.entity.File;
import com.bob.domain.file.entity.type.FileDomain;
import java.util.List;
import lombok.Builder;

@Builder
public record RegisterFileCommand(
    String domain,
    Object referenceId,
    List<String> fileNames
) {

  public List<File> toEntities() {
    return range(0, fileNames.size())
        .mapToObj(i -> File.builder()
            .fileName(fileNames.get(i))
            .domain(FileDomain.valueOf(domain))
            .referenceId(referenceId.toString())
            .sequence(i)
            .build())
        .toList();
  }
}
package com.bob.domain.file.service.dto.command;

import static com.bob.domain.file.entity.type.FileDomain.from;
import static java.util.stream.IntStream.range;

import com.bob.domain.file.entity.File;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ChangeFileCommand(
    String domain,
    String referenceId,
    List<String> fileNames,
    UUID memberId
) {

  public List<File> toEntities() {
    return range(0, fileNames.size())
        .mapToObj(i -> File.builder()
            .fileName(fileNames.get(i))
            .domain(from(domain))
            .referenceId(referenceId)
            .sequence(i)
            .uploader(memberId)
            .build())
        .toList();
  }
}

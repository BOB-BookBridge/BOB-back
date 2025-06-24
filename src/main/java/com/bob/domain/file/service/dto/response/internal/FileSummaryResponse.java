package com.bob.domain.file.service.dto.response.internal;

import com.bob.domain.file.entity.File;
import java.util.List;
import lombok.Builder;

@Builder
public record FileSummaryResponse(
    int sequence,
    String fileName
) {

  public static FileSummaryResponse from(File file) {
    return new FileSummaryResponse(file.getSequence(), file.getFileName());
  }

  public static List<FileSummaryResponse> from(List<File> files) {
    return files.stream()
        .map(file -> FileSummaryResponse.builder()
            .sequence(file.getSequence())
            .fileName(file.getFileName())
            .build()
        ).toList();
  }
}

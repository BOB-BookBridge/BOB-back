package com.bob.support.fixture.domain;

import com.bob.domain.file.entity.File;
import com.bob.domain.file.entity.type.FileDomain;
import java.util.List;

public class FileFixture {

  public static File defaultFile(String referenceId, String fileName, int sequence) {
    return File.builder()
        .fileName(fileName)
        .referenceId(referenceId)
        .domain(FileDomain.POST)
        .sequence(sequence)
        .build();
  }

  public static List<File> defaultFiles(String referenceId) {
    return List.of(
        defaultFile(referenceId, "post/1.jpg", 0),
        defaultFile(referenceId, "post/2.jpg", 1)
    );
  }
}

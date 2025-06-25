package com.bob.support.fixture.domain;

import com.bob.domain.file.entity.File;
import com.bob.domain.file.entity.type.FileDomain;
import java.util.List;

public class FileFixture {

  public static File defaultFile(String fileName, int sequence, String refId) {
    return File.builder()
        .fileName(fileName)
        .referenceId(refId)
        .domain(FileDomain.POST)
        .sequence(sequence)
        .build();
  }

  public static List<File> defaultFiles() {
    return List.of(
        defaultFile("post/1.jpg", 0, "1"),
        defaultFile("post/2.jpg", 1, "1"),
        defaultFile("post/3.jpg", 2, "1")
    );
  }

  public static List<File> customRefIdFiles(String refId) {
    return List.of(
        defaultFile("post/1.jpg", 0, refId),
        defaultFile("post/2.jpg", 1, refId),
        defaultFile("post/3.jpg", 2, refId)
    );
  }
}

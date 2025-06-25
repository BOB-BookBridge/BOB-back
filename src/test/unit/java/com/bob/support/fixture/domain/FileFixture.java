package com.bob.support.fixture.domain;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;

import com.bob.domain.file.entity.File;
import com.bob.domain.file.entity.type.FileDomain;
import java.util.List;
import java.util.UUID;

public class FileFixture {

  public static File defaultFile(String fileName, int sequence, String refId) {
    return File.builder()
        .fileName(fileName)
        .referenceId(refId)
        .domain(FileDomain.POST)
        .sequence(sequence)
        .uploader(MEMBER_ID)
        .build();
  }

  public static File otherFile(String fileName, int sequence, String refId) {
    return File.builder()
        .fileName(fileName)
        .referenceId(refId)
        .domain(FileDomain.POST)
        .sequence(sequence)
        .uploader(UUID.fromString("7f6d8b24-3ec0-4d8c-b9ef-9cb44d60af97"))
        .build();
  }

  public static List<File> defaultFiles() {
    return List.of(
        defaultFile("post/1.jpg", 0, "1"),
        defaultFile("post/2.jpg", 1, "1"),
        defaultFile("post/3.jpg", 2, "1")
    );
  }

  public static List<File> otherFiles() {
    return List.of(
        otherFile("post/1.jpg", 0, "2"),
        otherFile("post/2.jpg", 1, "2"),
        otherFile("post/3.jpg", 2, "2")
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

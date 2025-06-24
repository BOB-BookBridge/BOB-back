package com.bob.domain.file.service.dto.response;

import lombok.Builder;

@Builder
public record ReadSingleFileUploadUrlResponse(
    String fileName,
    String fileUploadUrl
) {

  public static ReadSingleFileUploadUrlResponse from(String fileName, String preSignedUrl) {
    return ReadSingleFileUploadUrlResponse.builder()
        .fileName(fileName)
        .fileUploadUrl(preSignedUrl)
        .build();
  }
}

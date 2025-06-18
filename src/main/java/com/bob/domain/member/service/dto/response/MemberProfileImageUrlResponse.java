package com.bob.domain.member.service.dto.response;

import lombok.Builder;

@Builder
public record MemberProfileImageUrlResponse(
    String fileName,
    String imageUploadUrl
) {

  public static MemberProfileImageUrlResponse of(String fileName, String imageUploadUrl) {
    return MemberProfileImageUrlResponse.builder()
        .fileName(fileName)
        .imageUploadUrl(imageUploadUrl)
        .build();
  }
}

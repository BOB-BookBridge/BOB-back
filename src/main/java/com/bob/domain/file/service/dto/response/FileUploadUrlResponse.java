package com.bob.domain.file.service.dto.response;

import static java.util.stream.IntStream.range;

import com.bob.domain.file.service.dto.response.internal.PreSignedUrlResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record FileUploadUrlResponse(
    List<PreSignedUrlResponse> urls
) {

  public static FileUploadUrlResponse from(List<String> fileNames, List<String> presignedUrls) {
    return FileUploadUrlResponse.builder()
        .urls((range(0, fileNames.size())
            .mapToObj(seq -> new PreSignedUrlResponse(seq, fileNames.get(seq), presignedUrls.get(seq)))
            .toList()
        )).build();
  }
}


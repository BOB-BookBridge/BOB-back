package com.bob.domain.post.service.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record PostFileSummaryResponse(
    List<FileSummary> images
) {

  public record FileSummary(
      int sequence,
      String fileName
  ) {

    public static FileSummary of(int sequence, String fileName) {
      return new FileSummary(sequence, fileName);
    }
  }
}

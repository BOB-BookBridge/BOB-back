package com.bob.domain.post.service.dto.response;

import com.bob.domain.file.service.dto.response.FilesResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record PostFileSummaryResponse(
    List<PostFileSummary> images
) {

  public static PostFileSummaryResponse from(FilesResponse response) {
    return new PostFileSummaryResponse(response.summaries().stream()
        .map(file -> PostFileSummary.of(file.sequence(), file.fileName()))
        .toList()
    );
  }
  
  public record PostFileSummary(
      int sequence,
      String fileName
  ) {

    public static PostFileSummary of(int sequence, String fileName) {
      return new PostFileSummary(sequence, fileName);
    }
  }
}

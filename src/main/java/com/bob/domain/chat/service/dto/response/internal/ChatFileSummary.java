package com.bob.domain.chat.service.dto.response.internal;

import com.bob.domain.file.service.dto.response.FilesResponse;
import java.util.List;

public record ChatFileSummary(
    int sequence,
    String fileName
) {

  public static ChatFileSummary of(int sequence, String fileName) {
    return new ChatFileSummary(sequence, fileName);
  }

  public static List<ChatFileSummary> convertFrom(FilesResponse response) {
    return response.summaries().stream()
        .map(file -> ChatFileSummary.of(file.sequence(), file.fileName()))
        .toList();
  }
}

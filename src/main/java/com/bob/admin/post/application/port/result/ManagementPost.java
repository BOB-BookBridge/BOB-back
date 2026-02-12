package com.bob.admin.post.application.port.result;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

@Builder
public record ManagementPost(
    Long id,
    String title,
    String thumbnailUrl,
    String description,
    String status,
    LocalDateTime createdAt,
    ManagementPostWriter writer,
    List<String> filterWords
) {

}

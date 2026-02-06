package com.bob.admin.post.application.port.result;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record ManagementPost(Long id, String title, String writerEmail, String status, LocalDateTime createdAt) {

}

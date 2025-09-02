package com.bob.domain.post.service.dto.command;

import com.bob.domain.post.entity.status.Status;
import java.util.UUID;

public record ChangeMemberPostStatusCommand(
    UUID memberId,
    Status status
) {

  public ChangeMemberPostStatusCommand of(UUID memberId, Status status) {
    return new ChangeMemberPostStatusCommand(memberId, status);
  }
}

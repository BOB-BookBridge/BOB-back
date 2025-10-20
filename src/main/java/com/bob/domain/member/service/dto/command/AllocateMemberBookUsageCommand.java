package com.bob.domain.member.service.dto.command;

import java.util.List;

public record AllocateMemberBookUsageCommand(
    List<Long> ids,
    Long usageId
) {

  public static AllocateMemberBookUsageCommand of(List<Long> ids, Long usageId) {
    return new AllocateMemberBookUsageCommand(ids, usageId);
  }
}

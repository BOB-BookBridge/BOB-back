package com.bob.domain.member.service.dto.command;

public record RemoveMemberBookUsageCommand(
    Long usageId
) {

  public static RemoveMemberBookUsageCommand of(Long usageId) {
    return new RemoveMemberBookUsageCommand(usageId);
  }
}

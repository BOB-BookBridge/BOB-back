package com.bob.domain.member.service.dto.command;

import java.util.List;

public record RemoveMemberBooksCommand(
    List<Long> ids
) {

  public static RemoveMemberBooksCommand of(List<Long> ids) {
    return new RemoveMemberBooksCommand(ids);
  }
}

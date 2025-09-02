package com.bob.web.member.request;

import com.bob.domain.member.service.dto.command.RecoverAccountCommand;

public record RecoverAccountRequest(
    String email
) {

  public RecoverAccountCommand toCommand() {
    return new RecoverAccountCommand(email);
  }
}

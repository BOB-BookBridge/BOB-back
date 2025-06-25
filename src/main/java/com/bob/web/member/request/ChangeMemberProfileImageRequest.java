package com.bob.web.member.request;

import com.bob.domain.member.service.dto.command.ChangeProfileImageCommand;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record ChangeMemberProfileImageRequest(
    @NotBlank(message = "fileName은 필수입니다.")
    String fileName
) {

  public ChangeProfileImageCommand toCommand(UUID memberId) {
    return new ChangeProfileImageCommand(memberId, fileName);
  }
}

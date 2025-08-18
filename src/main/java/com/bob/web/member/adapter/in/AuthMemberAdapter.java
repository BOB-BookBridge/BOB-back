package com.bob.web.member.adapter.in;

import com.bob.domain.member.service.dto.command.SocialLoginCommand;
import com.bob.domain.member.usecase.MemberWriteUseCase;
import com.bob.infra.auth.service.port.AuthMemberPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthMemberAdapter implements AuthMemberPort {

  private final MemberWriteUseCase writeUseCase;

  @Override
  public UUID socialLoginProcess(String provider, String email, String nickname) {
    return writeUseCase.socialLoginProcess(SocialLoginCommand.of(provider, email, nickname));
  }
}

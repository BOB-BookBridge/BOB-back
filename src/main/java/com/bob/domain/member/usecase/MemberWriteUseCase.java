package com.bob.domain.member.usecase;

import com.bob.domain.member.service.dto.command.CreateMemberCommand;
import com.bob.domain.member.service.dto.command.SocialLoginCommand;
import com.bob.domain.member.service.dto.response.SocialLoginResponse;

public interface MemberWriteUseCase {

  void signupProcess(CreateMemberCommand command);

  SocialLoginResponse socialLoginProcess(SocialLoginCommand command);
}

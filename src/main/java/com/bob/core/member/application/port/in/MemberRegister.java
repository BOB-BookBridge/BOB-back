package com.bob.core.member.application.port.in;

import com.bob.core.member.application.dto.command.CreateMemberCommand;
import com.bob.core.member.application.dto.command.SocialLoginCommand;
import com.bob.core.member.domain.Member;

public interface MemberRegister {

    Member signup(CreateMemberCommand command);

    Member socialLogin(SocialLoginCommand command);
}

package com.bob.core.application.member.port.in;

import com.bob.core.application.member.dto.command.CreateMemberCommand;
import com.bob.core.application.member.dto.command.SocialLoginCommand;
import com.bob.core.domain.member.Member;

public interface MemberRegister {

    Member signup(CreateMemberCommand command);

    Member socialLogin(SocialLoginCommand command);
}

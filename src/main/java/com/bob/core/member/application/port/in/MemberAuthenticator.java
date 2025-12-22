package com.bob.core.member.application.port.in;

import com.bob.core.member.application.dto.command.VerifyMailCommand;

public interface MemberAuthenticator {

    String sendAuthenticationCode(String email);

    void verifyMail(String email, VerifyMailCommand command);
}

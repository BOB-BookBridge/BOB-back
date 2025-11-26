package com.bob.core.application.member.port.in;

import com.bob.core.application.member.dto.command.VerifyMailCommand;

public interface MemberAuthenticator {

    String sendAuthenticationCode(String email);

    void verifyMail(String email, VerifyMailCommand command);
}

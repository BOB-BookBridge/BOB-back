package com.bob.core.application.member;

import static com.bob.global.utils.random.RandomUtils.generateCode;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.application.member.dto.command.VerifyMailCommand;
import com.bob.core.application.member.port.in.MemberAuthenticator;
import com.bob.core.application.member.port.out.MailSender;
import com.bob.core.application.member.port.out.MemberCachePort;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberAuthenticateService implements MemberAuthenticator {

    private final MailSender mailSender;

    private final MemberCachePort cachePort;

    @Override
    public String sendAuthenticationCode(String email) {
        String authenticationCode = generateCode(6);

        mailSender.send(email, "인증 코드", authenticationCode);

        cachePort.setAuthenticationCode(email, authenticationCode);

        return authenticationCode;
    }

    @Override
    public void verifyMail(String email, VerifyMailCommand command) {
        checkCode(email, command.code());

        cachePort.setAuthenticationSuccess(email);
    }

    private void checkCode(String email, String code) {
        Optional<String> value = cachePort.get(email);
        if (value.isEmpty())
            throw new ApplicationException(ApplicationError.EXPIRED_MAIL_CODE);

        if (!value.get().equals(code))
            throw new ApplicationException(ApplicationError.INVALID_MAIL_CODE);
    }
}

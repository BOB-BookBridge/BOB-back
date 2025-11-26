package com.bob.core.application.member.port.in;

import static com.bob.global.exception.response.ApplicationError.EXPIRED_MAIL_CODE;
import static com.bob.global.exception.response.ApplicationError.INVALID_MAIL_CODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.application.member.dto.command.VerifyMailCommand;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.infrastructure.cache.repository.KeyValueRepository;
import com.bob.support.annotation.ContainerTest;

@DisplayName("회원 인증 테스트")
@ContainerTest
record MemberAuthenticatorTest(MemberAuthenticator memberAuthenticator, KeyValueRepository keyValueRepository) {

    @Test
    void 이메일_인증_코드_발송() {
        String email = "test@example.com";

        String code = memberAuthenticator.sendAuthenticationCode(email);

        Optional<String> cacheValue = keyValueRepository.get("auth:test@example.com");
        assertThat(cacheValue).isPresent();
        assertThat(cacheValue.get()).isEqualTo(code);
    }

    @Test
    void 이메일_인증() {
        String email = "test@example.com";
        String code = memberAuthenticator.sendAuthenticationCode(email);

        VerifyMailCommand command = new VerifyMailCommand(code);
        memberAuthenticator.verifyMail(email, command);

        Optional<String> cacheValue = keyValueRepository.get("auth:test@example.com");
        assertThat(cacheValue).isPresent();
        assertThat(cacheValue.get()).isEqualTo("success");
    }

    @Test
    void 이메일_인증_시_캐시_저장소에_존재하지_않는_코드라면_사용자_예외가_발생한다() {
        String email = "test@example.com";
        String code = memberAuthenticator.sendAuthenticationCode(email);
        keyValueRepository.delete("auth:test@example.com");

        VerifyMailCommand command = new VerifyMailCommand(code);
        assertThatThrownBy(() -> memberAuthenticator.verifyMail(email, command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(EXPIRED_MAIL_CODE.getMessage());
    }

    @Test
    void 이메일_인증_시_일치하지_않는_코드라면_사용자_예외가_발생한다() {
        String email = "test@example.com";
        String code = memberAuthenticator.sendAuthenticationCode(email);

        VerifyMailCommand command = new VerifyMailCommand("!" + code);
        assertThatThrownBy(() -> memberAuthenticator.verifyMail(email, command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(INVALID_MAIL_CODE.getMessage());
    }
}

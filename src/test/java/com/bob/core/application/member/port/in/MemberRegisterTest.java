package com.bob.core.application.member.port.in;

import static com.bob.core.domain.member.Status.ACTIVE;
import static com.bob.support.fixture.member.domain.MemberFixture.createMember;
import static com.bob.support.fixture.member.dto.command.CreateMemberCommandFixture.createCreateMemberCommand;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.application.member.dto.command.CreateMemberCommand;
import com.bob.core.application.member.dto.command.SocialLoginCommand;
import com.bob.core.application.member.port.out.MemberCachePort;
import com.bob.core.domain.member.Member;
import com.bob.core.domain.member.PasswordEncoder;
import com.bob.core.domain.member.repository.MemberRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import com.bob.support.annotation.ContainerTest;

@DisplayName("회원 등록 테스트")
@ContainerTest
record MemberRegisterTest(
    MemberRegister memberRegister, MemberRepository memberRepository, PasswordEncoder encoder,
    MemberCachePort memberCachePort
) {

    @Test
    void 회원가입() {
        CreateMemberCommand command = createCreateMemberCommand();

        memberCachePort.setAuthenticationSuccess(command.email()); // 이메일 인증 성공 설정

        Member member = memberRegister.signup(command);

        assertThat(member.getId()).isNotNull();
        assertThat(member.getEmail()).isEqualTo(command.email());
        assertThat(member.getNickname()).isEqualTo(command.nickname());
        assertThat(member.getStatus()).isEqualTo(ACTIVE);
        assertThat(member.getArea().getAuthenticatedAt()).isCloseTo(LocalDate.now(), within(1, DAYS));
        assertThat(encoder.matches(command.password(), member.getPassword())).isTrue();
    }

    @Test
    void 회원가입_시_이메일_인증이_되지_않았다면_사용자_예외가_발생한다() {
        CreateMemberCommand command = createCreateMemberCommand();

        assertThatThrownBy(() -> memberRegister.signup(command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ApplicationError.UNVERIFIED_EMAIL.getMessage());
    }

    @Test
    void 회원가입_시_이미_등록된_이메일로_요청한다면_사용자_예외가_발생한다() {
        String duplicateEmail = "test@email.com";
        memberCachePort.setAuthenticationSuccess(duplicateEmail);

        Member existing = createMember(duplicateEmail);
        memberRepository.save(existing);

        CreateMemberCommand command = createCreateMemberCommand();

        assertThatThrownBy(() -> memberRegister.signup(command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ApplicationError.ALREADY_EXISTS_EMAIL.getMessage());

        memberCachePort.delete(duplicateEmail);
    }

    @Test
    void 소셜_회원가입() {
        String email = "test@naver.com";
        String nickname = "foo";
        SocialLoginCommand command = SocialLoginCommand.of("NAVER", email, nickname);

        Member member = memberRegister.socialLogin(command);

        assertThat(member.getId()).isNotNull();
        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(member.getNickname()).isEqualTo(nickname);
        assertThat(member.getStatus()).isEqualTo(ACTIVE);
        assertThat(member.getPassword()).isNull();
        assertThat(member.getArea().getAuthenticatedAt()).isEqualTo(LocalDate.EPOCH);
    }

    @Test
    void 소셜_로그인() {
        String email = "test@google.com";
        Member existing = memberRepository.save(createMember(email));
        SocialLoginCommand command = SocialLoginCommand.of("GOOGLE", email, "foo");

        Member member = memberRegister.socialLogin(command);

        assertThat(member.getId()).isEqualTo(existing.getId());
    }
}

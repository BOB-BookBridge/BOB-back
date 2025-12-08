package com.bob.core.adapter.member.out;

import static com.bob.support.fixture.member.domain.MemberFixture.createMember;
import static com.bob.support.fixture.member.domain.MemberFixture.createSocialMember;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.domain.member.Member;
import com.bob.core.domain.member.repository.MemberRepository;
import com.bob.security.application.port.dto.AuthMember;
import com.bob.security.application.port.dto.SocialAuthMember;
import com.bob.support.annotation.ContainerTest;

@DisplayName("회원 불러오기 테스트")
@ContainerTest
record MemberLoaderAdapterTest(MemberLoaderAdapter memberLoaderAdapter, MemberRepository memberRepository) {

    @Test
    void 회원_불러오기() {
        Member member = memberRepository.save(createMember());

        Optional<AuthMember> found = memberLoaderAdapter.load("test@email.com");

        assertThat(found).isPresent();

        AuthMember loaded = found.get();
        assertThat(loaded.id()).isEqualTo(member.getId());
        assertThat(loaded.role()).isEqualTo("USER");
    }

    @Test
    void 회원_불러오기_시_존재하지_않으면_empty_반환() {
        Optional<AuthMember> found = memberLoaderAdapter.load("test@email.com");

        assertThat(found).isEmpty();
    }

    @Test
    void 소셜_회원_불러오기() {
        Member socialMember = memberRepository.save(createSocialMember());

        SocialAuthMember loaded = memberLoaderAdapter.load("GOOGLE", "test@google.com", "tester");

        assertThat(loaded.id()).isEqualTo(socialMember.getId());
        assertThat(loaded.role()).isEqualTo("USER");
    }
}

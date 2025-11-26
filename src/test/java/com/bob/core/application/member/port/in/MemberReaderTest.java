package com.bob.core.application.member.port.in;

import static com.bob.global.exception.response.ApplicationError.NOT_EXISTS_MEMBER;
import static com.bob.support.fixture.member.domain.MemberFixture.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.application.member.dto.result.MemberDetail;
import com.bob.core.domain.member.Member;
import com.bob.core.domain.member.repository.MemberRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.annotation.ContainerTest;

@DisplayName("회원 조회 테스트")
@ContainerTest
record MemberReaderTest(MemberReader memberReader, MemberRepository memberRepository, EntityManager em) {

    @Test
    void ID_기반_회원_조회() {
        Member member = memberRepository.save(createMember());
        UUID memberId = member.getId();

        em.flush();
        em.clear();

        Member found = memberReader.read(memberId);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(memberId);
    }

    @Test
    void 존재하지_않는_회원_UUID_조회_시_예외가_발생한다() {
        UUID nonExistentId = UUID.randomUUID();

        assertThatThrownBy(() -> memberReader.read(nonExistentId))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(NOT_EXISTS_MEMBER.getMessage());
    }

    @Test
    void Email_기반_회원_조회() {
        Member member = memberRepository.save(createMember());
        String email = member.getEmail();

        em.flush();
        em.clear();

        Member found = memberReader.read(member.getEmail());

        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo(email);
    }

    @Test
    void 존재하지_않는_회원_이메일_조회_시_예외가_발생한다() {
        assertThatThrownBy(() -> memberReader.read("nonexistent@email.com"))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(NOT_EXISTS_MEMBER.getMessage());
    }

    @Test
    void 회원_상세_조회() {
        Member member = memberRepository.save(createMember());
        MemberDetail detail = memberReader.readDetail(member.getId(), false);

        assertThat(detail).isNotNull();
        assertThat(detail.id()).isEqualTo(member.getId());
        assertThat(detail.area()).isNotNull();
        assertThat(detail.bookcase()).isNotNull();
        assertThat(detail.wishes()).isNotNull();
    }

    @Test
    void 비활성화_회원_상세_조회() {
        Member member = memberRepository.save(createMember());
        member.deactivate();

        MemberDetail detail = memberReader.readDetail(member.getId(), false);

        assertThat(detail).isNotNull();
        assertThat(detail.id()).isEqualTo(member.getId());
        assertThat(detail.email()).isEqualTo("delete");
        assertThat(detail.nickname()).isEqualTo("(알 수 없음)");
        assertThat(detail.area()).isNotNull();
        assertThat(detail.bookcase()).isNotNull();
        assertThat(detail.wishes()).isNotNull();
    }
}

package com.bob.core.member.application.port.in;

import static com.bob.support.fixture.member.domain.MemberFixture.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.member.application.dto.result.MemberBasicInfo;
import com.bob.core.member.application.dto.result.MemberDetail;
import com.bob.core.member.domain.Member;
import com.bob.core.member.domain.repository.MemberRepository;
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
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("회원을 찾을 수 없습니다.");
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
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("회원을 찾을 수 없습니다.");
    }

    @Test
    void 회원_기본_정보_조회() {
        Member member = memberRepository.save(createMember());

        MemberBasicInfo basicInfo = memberReader.readBasicInfo(member.getId());

        assertThat(basicInfo).isNotNull();
        assertThat(basicInfo.id()).isEqualTo(member.getId());
        assertThat(basicInfo.nickname()).isEqualTo("tester");
        assertThat(basicInfo.profileImageUrl()).isNull();
    }

    @Test
    void 내_정보_상세_조회() {
        Member member = memberRepository.save(createMember());
        LocalDateTime standardTime = LocalDateTime.now();

        MemberDetail detail = memberReader.readDetail(member.getId(), true);

        assertThat(detail).isNotNull();
        assertThat(detail.id()).isEqualTo(member.getId());
        assertThat(detail.role()).isEqualTo(member.getRole().name());
        assertThat(detail.area()).isNotNull();
        assertThat(detail.bookcase()).isNotNull();
        assertThat(detail.wishes()).isNotNull();
        assertThat(detail.lastActiveAt()).isAfter(standardTime);
    }

    @Test
    void 회원_상세_조회() {
        Member member = memberRepository.save(createMember());
        MemberDetail detail = memberReader.readDetail(member.getId(), false);

        assertThat(detail).isNotNull();
        assertThat(detail.id()).isEqualTo(member.getId());
        assertThat(detail.role()).isEqualTo(member.getRole().name());
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
        assertThat(detail.role()).isEqualTo(member.getRole().name());
        assertThat(detail.email()).isEqualTo("delete");
        assertThat(detail.nickname()).isEqualTo("(알 수 없음)");
        assertThat(detail.area()).isNotNull();
        assertThat(detail.bookcase()).isNotNull();
        assertThat(detail.wishes()).isNotNull();
    }
}

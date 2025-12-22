package com.bob.core.member.application.port.in;

import static com.bob.global.exception.response.ApplicationError.MEMBER_WISH_DUPLICATED;
import static com.bob.support.fixture.member.domain.MemberFixture.createMember;
import static com.bob.support.fixture.member.dto.command.RegisterMemberWishCommandFixture.createRegisterMemberWishCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.member.application.dto.command.RegisterMemberWishCommand;
import com.bob.core.member.application.dto.command.RemoveMemberWishCommand;
import com.bob.core.member.domain.Member;
import com.bob.core.member.domain.repository.MemberRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.annotation.ContainerTest;

@DisplayName("회원 희망 도서 관리 테스트")
@ContainerTest
record MemberWishManagerTest(MemberWishManager memberWishManager, MemberRepository memberRepository, EntityManager em) {

    @Test
    void 회원_희망_도서_등록() {
        Member member = memberRepository.save(createMember());
        assertThat(member.getWishes().size()).isZero();

        RegisterMemberWishCommand command = createRegisterMemberWishCommand("0000000000000");
        memberWishManager.registerWish(member.getId(), command);

        command = createRegisterMemberWishCommand("1111111111111");

        em.flush();
        em.clear();

        Member after = memberWishManager.registerWish(member.getId(), command);

        assertThat(after.getWishes()).hasSize(2);
    }

    @Test
    void 회원_희망_도서_등록_시_중복된_희망_도서라면_사용자_예외가_발생한다() {
        Member member = memberRepository.save(createMember());
        RegisterMemberWishCommand command = createRegisterMemberWishCommand();
        memberWishManager.registerWish(member.getId(), command);

        assertThatThrownBy(() -> memberWishManager.registerWish(member.getId(), command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(MEMBER_WISH_DUPLICATED.getMessage());
    }

    @Test
    void 회원_희망_도서_삭제() {
        Member member = memberRepository.save(createMember());
        memberWishManager.registerWish(member.getId(), createRegisterMemberWishCommand());
        assertThat(member.getWishes()).hasSize(1);

        RemoveMemberWishCommand command = new RemoveMemberWishCommand(member.getWishes().get(0).getId());
        Member after = memberWishManager.removeWish(member.getId(), command);

        assertThat(after.getWishes()).hasSize(0);
    }
}

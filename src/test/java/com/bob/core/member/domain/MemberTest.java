package com.bob.core.member.domain;

import static com.bob.support.fixture.member.domain.MemberFixture.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.support.fixture.member.domain.MemberFixture;

@DisplayName("회원 테스트")
class MemberTest {

    @Test
    void 회원_생성() {
        String email = "test@email.com";
        String password = "password";
        String nickname = "tester";
        int emdAreaId = 1;

        Member member = Member.createMember(email, password, nickname, emdAreaId);

        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(member.getPassword()).isEqualTo(password);
        assertThat(member.getNickname()).isEqualTo(nickname);
        assertThat(member.getArea().getEmdId()).isEqualTo(emdAreaId);
        assertThat(member.getInterests()).isEmpty();
        assertThat(member.getWishes()).isEmpty();
    }

    @Test
    void 소셜_회원_생성() {
        String email = "social@email.com";
        String provider = "GOOGLE";
        String nickname = "socialUser";

        Member member = Member.createSocialMember(email, provider, nickname);

        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(member.getPassword()).isNull();
        assertThat(member.getNickname()).isEqualTo(nickname);
        assertThat(member.getInterests()).isEmpty();
        assertThat(member.getWishes()).isEmpty();
    }

    @Test
    void 회원_상태_강제_변경() {
        Member member = MemberFixture.createMember();
        member.updateStatusFromAdmin(Status.DEACTIVATED, "비활성화");
        assertThat(member.isDeactivated()).isTrue();
        assertThat(member.getMemo()).isEqualTo("비활성화");

        member.updateStatusFromAdmin(Status.BANNED, "제한");
        assertThat(member.isBanned()).isTrue();
        assertThat(member.getMemo()).isEqualTo("제한");
    }

    @Test
    void 회원_상태_강제_변경_시_동일한_상태로_변경하면_예외가_발생한다() {
        Member member = MemberFixture.createMember();
        assertThat(member.getStatus()).isEqualTo(Status.ACTIVE);

        assertThatThrownBy(() -> member.updateStatusFromAdmin(Status.ACTIVE, "동일 상태로 변경"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("현재 상태와 변경하려는 상태가 동일합니다");
    }

    @Test
    void 회원_정보_수정() {
        Member member = MemberFixture.createMember();

        String newNickname = "updatedNickname";

        int newEmdId = 99;

        List<Long> interestIds = java.util.List.of(1L, 2L);
        List<String> interestNames = java.util.List.of("Java", "Spring");

        member.updateInfo(newNickname, newEmdId, interestIds, interestNames);

        assertThat(member.getNickname()).isEqualTo(newNickname);
        assertThat(member.getArea().getEmdId()).isEqualTo(newEmdId);
        assertThat(member.getInterests()).hasSize(2);
        assertThat(member.getInterests().get(0).getInterestId()).isEqualTo(1L);
        assertThat(member.getInterests().get(1).getInterestId()).isEqualTo(2L);
    }

    @Test
    void 회원_비밀번호_수정() {
        Member member = MemberFixture.createMember();

        member.updatePassword("newPassword");

        assertThat(member.getPassword()).isNotEqualTo(createMember().getPassword());
        assertThat(member.getPassword()).isEqualTo("newPassword");
    }

    @Test
    void 회원_프로필_이미지_URL_수정() {
        Member member = MemberFixture.createMember();
        String newProfileImageUrl = "profile/test.jpg";

        member.updateProfileImageUrl(newProfileImageUrl);

        assertThat(member.getProfileImageUrl()).isEqualTo(newProfileImageUrl);
    }

    @Test
    void 회원_희망_도서_추가() {
        Member member = MemberFixture.createMember();
        Long bookId = 1L;

        member.addWish(bookId);

        assertThat(member.getWishes()).hasSize(1);
        assertThat(member.getWishes().get(0).getBookId()).isEqualTo(bookId);
    }

    @Test
    void 회원_희망_도서_제거() {
        Member member = MemberFixture.createMember();
        member.addWish(1L);
        Long wishId = member.getWishes().get(0).getId();

        member.removeWish(wishId);

        assertThat(member.getWishes()).isEmpty();
    }

    @Test
    void 회원_활성화() {
        Member member = MemberFixture.createMember();
        member.deactivate();

        member.activate();

        assertThat(member.isDeactivated()).isFalse();
    }

    @Test
    void 회원_비활성화() {
        Member member = MemberFixture.createMember();

        member.deactivate();

        assertThat(member.isDeactivated()).isTrue();
    }

    @Test
    void 회원_정지() {
        Member member = MemberFixture.createMember();

        member.ban();

        assertThat(member.isBanned()).isTrue();
    }

    @Test
    void 회원_마지막_활동시간_갱신() {
        Member member = MemberFixture.createMember();
        assertThat(member.getLastActiveAt()).isNull();

        member.updateLastActiveTime();

        assertThat(member.getLastActiveAt()).isNotNull();
    }

    @Test
    void 회원_메모_갱신() {
        Member member = MemberFixture.createMember();
        assertThat(member.getMemo()).isNull();

        member.updateMemo("memo");

        assertThat(member.getMemo()).isEqualTo("memo");
    }
}

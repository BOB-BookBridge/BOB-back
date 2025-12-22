package com.bob.core.member.application.port.in;

import static com.bob.core.member.domain.Status.ACTIVE;
import static com.bob.core.member.domain.Status.BANNED;
import static com.bob.core.member.domain.Status.DEACTIVATED;
import static com.bob.global.exception.response.ApplicationError.MEMBER_BANNED;
import static com.bob.global.exception.response.ApplicationError.MEMBER_PASSWORD_MISMATCH;
import static com.bob.global.exception.response.ApplicationError.NO_CHANGES;
import static com.bob.support.fixture.area.domain.AreaFixture.CENTER_LAT;
import static com.bob.support.fixture.area.domain.AreaFixture.CENTER_LON;
import static com.bob.support.fixture.area.domain.AreaFixture.EMD_AREA_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.createCustomPasswordMember;
import static com.bob.support.fixture.member.domain.MemberFixture.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpServletResponse;

import com.bob.core.member.application.dto.command.ChangePasswordCommand;
import com.bob.core.member.application.dto.command.ChangeProfileCommand;
import com.bob.core.member.application.dto.command.ChangeProfileImageCommand;
import com.bob.core.member.application.dto.command.ChangeStatusCommand;
import com.bob.core.member.domain.Member;
import com.bob.core.member.domain.MemberArea;
import com.bob.core.member.domain.PasswordEncoder;
import com.bob.core.member.domain.repository.MemberRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.annotation.ContainerTest;

@DisplayName("회원 정보 변경 테스트")
@ContainerTest
record MemberModifierTest(
    MemberModifier memberModifier, MemberRepository memberRepository,
    PasswordEncoder passwordEncoder
) {

    @Test
    void 상태_변경() {
        Member member = memberRepository.save(createMember());
        ChangeStatusCommand command = new ChangeStatusCommand("BANNED", "신고 누적");

        member = memberModifier.changeStatus(member.getId(), command);

        assertThat(member.getStatus()).isEqualTo(BANNED);
        assertThat(member.getMemo()).isEqualTo("신고 누적");
    }

    @Test
    void 비밀번호_수정() {
        Member member = memberRepository.save(createCustomPasswordMember(passwordEncoder.encode("password")));

        var command = new ChangePasswordCommand("password", "newPassword");

        member = memberModifier.changePassword(member.getId(), command);

        assertThat(passwordEncoder.matches("newPassword", member.getPassword())).isTrue();
    }

    @Test
    void 비밀번호_수정_시_입력한_비밀번호가_다르면_사용자_예외가_발생한다() {
        Member member = memberRepository.save(createCustomPasswordMember(passwordEncoder.encode("password")));

        var command = new ChangePasswordCommand("invalid", "newPassword");

        assertThatThrownBy(() -> memberModifier.changePassword(member.getId(), command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(MEMBER_PASSWORD_MISMATCH.getMessage());
    }

    @Test
    void 프로필_정보_수정() {
        Member member = memberRepository.save(createMember());

        List<String> interests = List.of("소설", "에세이");
        var command = new ChangeProfileCommand("newNickname", EMD_AREA_ID, true, CENTER_LAT, CENTER_LON, interests);

        member = memberModifier.changeProfile(member.getId(), command);

        assertThat(member.getNickname()).isEqualTo("newNickname");
        assertThat(member.getArea().getEmdId()).isEqualTo(EMD_AREA_ID);
        assertThat(member.getInterests()).hasSize(2);
    }

    @Test
    void 프로필_정보_수정_활동지역_인증_제외() {
        Member member = memberRepository.save(createMember());
        int current = member.getArea().getEmdId();

        List<String> interests = List.of("소설", "에세이");
        var command = new ChangeProfileCommand("newNickname", 999, false, CENTER_LAT, CENTER_LON, interests);

        member = memberModifier.changeProfile(member.getId(), command);

        assertThat(member.getNickname()).isEqualTo("newNickname");
        assertThat(member.getInterests()).hasSize(2);

        assertThat(member.getArea().getEmdId()).isNotEqualTo(999);
        assertThat(member.getArea().getEmdId()).isEqualTo(current);
    }

    @Test
    void 프로필_정보_수정_변경사항_없음_및_위치_인증_여부_true() {
        Member member = memberRepository.save(createMember("test@example.com", "password", "nickname", EMD_AREA_ID));
        var command = new ChangeProfileCommand("nickname", EMD_AREA_ID, true, CENTER_LAT, CENTER_LON, List.of());

        assertDoesNotThrow(() -> memberModifier.changeProfile(member.getId(), command));
    }

    @Test
    void 프로필_정보_수정_시_변경사항이_없으면_사용자_예외가_발생한다() {
        Member member = memberRepository.save(createMember());
        String nickname = member.getNickname();
        MemberArea area = member.getArea();

        var command = new ChangeProfileCommand(nickname, area.getEmdId(), false, CENTER_LAT, CENTER_LON, List.of());

        assertThatThrownBy(() -> memberModifier.changeProfile(member.getId(), command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(NO_CHANGES.getMessage());
    }

    @Test
    void 프로필_이미지_수정() {
        Member member = memberRepository.save(createMember());

        String newImageUrl = "https://new-profile-image.jpg";
        var command = new ChangeProfileImageCommand(newImageUrl);

        member = memberModifier.changeProfileImage(member.getId(), command);

        assertThat(member.getProfileImageUrl()).isEqualTo(newImageUrl);
    }

    @Test
    void 임시_비밀번호_발급() {
        Member member = memberRepository.save(createMember());
        String originalPassword = member.getPassword();

        member = memberModifier.issueTempPassword(member.getEmail());

        assertThat(member.getPassword()).isNotEqualTo(originalPassword);
    }

    @Test
    void 회원_활성화() {
        Member member = memberRepository.save(createMember());
        member.deactivate();

        member = memberModifier.activate(member.getEmail());

        assertThat(member.getStatus()).isEqualTo(ACTIVE);
    }

    @Test
    void 회원_활성화_시_차단된_회원이면_예외가_발생한다() {
        Member member = memberRepository.save(createMember());
        member.ban();

        assertThatThrownBy(() -> memberModifier.activate(member.getEmail()))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(MEMBER_BANNED.getMessage());
    }

    @Test
    void 회원_비활성화() {
        Member member = memberRepository.save(createMember());
        MockHttpServletResponse response = new MockHttpServletResponse();

        member = memberModifier.deactivate(member.getId(), response);

        assertThat(member.getStatus()).isEqualTo(DEACTIVATED);
    }
}

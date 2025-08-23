package com.bob.domain.member.service;

import static com.bob.support.fixture.command.ChangeProfileCommandFixture.defaultChangeProfileCommand;
import static com.bob.support.fixture.command.ChangeProfileCommandFixture.sameNicknameChangeProfileCommand;
import static com.bob.support.fixture.command.ChangeProfileImageUrlCommandFixture.customChangeProfileImageUrlCommand;
import static com.bob.support.fixture.command.MemberCommandFixture.customChangePasswordCommand;
import static com.bob.support.fixture.command.MemberCommandFixture.defaultCreateMemberCommand;
import static com.bob.support.fixture.command.MemberCommandFixture.defaultIssuePasswordCommand;
import static com.bob.support.fixture.domain.MemberFixture.customEmailMember;
import static com.bob.support.fixture.domain.MemberFixture.defaultMember;
import static com.bob.support.fixture.domain.MemberFixture.encryptPasswordMember;
import static com.bob.support.fixture.response.MemberAreaSummaryResponseFixture.DEFAULT_AREA_SUMMARY_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.bob.domain.member.entity.Member;
import com.bob.domain.member.repository.MemberRepository;
import com.bob.domain.member.service.dto.command.ChangePasswordCommand;
import com.bob.domain.member.service.dto.command.ChangeProfileCommand;
import com.bob.domain.member.service.dto.command.ChangeProfileImageCommand;
import com.bob.domain.member.service.dto.command.CreateMemberCommand;
import com.bob.domain.member.service.dto.command.IssuePasswordCommand;
import com.bob.domain.member.service.dto.command.RemoveMemberCommand;
import com.bob.domain.member.service.dto.command.SocialLoginCommand;
import com.bob.domain.member.service.dto.query.ReadProfileQuery;
import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import com.bob.domain.member.service.port.out.MemberAreaPort;
import com.bob.domain.member.service.port.out.MemberMailPort;
import com.bob.domain.member.service.port.out.MemberRedisPort;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import com.bob.support.TestContainerSupport;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("사용자 서비스 통합 테스트")
@Transactional
@SpringBootTest
class MemberServiceIntgTest extends TestContainerSupport {

  @Autowired
  private MemberService memberService;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @MockitoBean
  private MemberAreaPort areaPort;

  @MockitoBean
  private MemberRedisPort redisPort;

  @MockitoBean
  private MemberMailPort mailPort;

  @Test
  @DisplayName("회원가입 - 성공 테스트")
  void 이메일_인증이_완료된_사용자는_회원가입을_할_수_있다() {
    // given
    String email = "test@email.com";
    CreateMemberCommand command = defaultCreateMemberCommand();
    given(redisPort.isVerified(email)).willReturn(true);

    // when
    memberService.signupProcess(command);

    // then
    Member member = memberRepository.findByEmail(email).orElseThrow();
    assertThat(member.getEmail()).isEqualTo(email);
    assertThat(passwordEncoder.matches(command.password(), member.getPassword())).isTrue();
    assertThat(member.getNickname()).isEqualTo(command.nickname());
  }

  @Test
  @DisplayName("회원가입 - 실패 테스트(이메일 인증 X)")
  void 이메일_인증을_하지않은_사용자는_회원가입을_할_수_없다() {
    // given
    CreateMemberCommand command = defaultCreateMemberCommand();

    // when & then
    assertThatThrownBy(() -> memberService.signupProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.UNVERIFIED_EMAIL.getMessage());
  }

  @Test
  @DisplayName("회원가입 - 실패 테스트(중복된 이메일)")
  void 동일한_이메일_계정이_존재하면_사용자는_회원가입을_할_수_없다() {
    // given
    String duplicateEmail = "test@email.com";
    given(redisPort.isVerified(duplicateEmail)).willReturn(true);

    Member existing = customEmailMember(duplicateEmail);
    memberRepository.save(existing);
    CreateMemberCommand command = defaultCreateMemberCommand();

    // when & then
    assertThatThrownBy(() -> memberService.signupProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.ALREADY_EXISTS_EMAIL.getMessage());
  }

  @Test
  @DisplayName("소셜 로그인 - 존재하지 않는 회원의 회원가입 테스트")
  void 동일한_이메일을_가진_회원이_존재하지_않는_경우_회원가입을_진행하고_미인증_활동지역을_생성한다() {
    // given
    String email = "test@naver.com";
    String nickname = "foo";
    SocialLoginCommand command = SocialLoginCommand.of("NAVER", email, nickname);

    // when
    UUID result = memberService.socialLoginProcess(command);

    // then
    Member saved = memberRepository.findByEmail(email).orElseThrow();
    assertThat(saved.getEmail()).isEqualTo(email);
    assertThat(saved.getNickname()).isEqualTo(nickname);
    assertThat(result).isEqualTo(saved.getId());
    then(areaPort).should(times(1)).createNonAuthenticatedActivityArea(saved.getId(), command.emdId());
  }

  @Test
  @DisplayName("소셜 로그인 - 존재하는 회원의 로그인 테스트")
  void 동일한_이메일을_가진_회원이_존재하는_경우_회원_ID를_반환한다() {
    // given
    String email = "test@google.com";
    Member existing = memberRepository.save(customEmailMember(email));
    SocialLoginCommand command = SocialLoginCommand.of("GOOGLE", email, "foo");

    // when
    UUID result = memberService.socialLoginProcess(command);

    // then
    assertThat(result).isEqualTo(existing.getId());
    then(areaPort).should(never()).createNonAuthenticatedActivityArea(any(), any());
  }

  @Test
  @DisplayName("사용자 프로필 조회 - 성공 테스트")
  void 회원은_자신의_프로필을_조회할_수_있다() {
    // given
    Member member = defaultMember();
    memberRepository.save(member);
    ReadProfileQuery query = ReadProfileQuery.of(member.getId());
    given(areaPort.readMemberAreaSummary(member.getId())).willReturn(DEFAULT_AREA_SUMMARY_RESPONSE);

    // when
    MemberProfileResponse response = memberService.readProfileProcess(query);

    // then
    assertThat(response.memberId()).isEqualTo(member.getId());
    assertThat(response.nickname()).isEqualTo(member.getNickname());
    assertThat(response.profileImageUrl()).isEqualTo(member.getProfileImageUrl());
    assertThat(response.area()).isNotNull();
  }

  @Test
  @DisplayName("프로필 변경 - 성공 테스트")
  void 닉네임이_다르면_프로필을_변경할_수_있다() {
    // given
    Member member = defaultMember();
    memberRepository.save(member);
    ChangeProfileCommand command = defaultChangeProfileCommand(member.getId());

    // when
    memberService.changeProfileProcess(command);

    // then
    assertThat(member.getNickname()).isEqualTo(command.nickname());
  }

  @Test
  @DisplayName("프로필 변경 - 실패 테스트(동일한 닉네임)")
  void 닉네임이_동일하면_프로필_변경에_실패한다() {
    // given
    Member member = defaultMember();
    memberRepository.save(member);

    ChangeProfileCommand command = sameNicknameChangeProfileCommand(member.getId());

    // when & then
    assertThatThrownBy(() -> memberService.changeProfileProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.IS_SAME_REQUEST.getMessage());
  }

  @Test
  @DisplayName("임시 비밀번호 발급 - 성공 테스트")
  void 이메일로_임시_비밀번호를_받아_비밀번호를_변경할_수_있다() {
    // given
    Member member = defaultMember();
    memberRepository.save(member);
    IssuePasswordCommand command = defaultIssuePasswordCommand();

    // when
    memberService.issueTempPasswordProcess(command);

    // then
    assertThat(member.getPassword()).isNotEqualTo(defaultMember().getPassword());
  }

  @Test
  @DisplayName("임시 비밀번호 발급 - 실패 테스트(존재하지 않는 이메일)")
  void 존재하지_않는_이메일이면_임시_비밀번호_발급에_실패한다() {
    // given
    String notExistsEmail = "unknown@email.com";
    IssuePasswordCommand command = new IssuePasswordCommand(notExistsEmail);

    // when & then
    assertThatThrownBy(() -> memberService.issueTempPasswordProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.NOT_EXISTS_MEMBER.getMessage());
  }

  @Test
  @DisplayName("비밀번호 변경 - 성공 테스트")
  void 기존_비밀번호가_일치하면_비밀번호를_변경할_수_있다() {
    // given
    String password = "password";
    String newPassword = "new-password";
    Member member = encryptPasswordMember(passwordEncoder.encode(password));
    memberRepository.save(member);
    ChangePasswordCommand command = customChangePasswordCommand(member.getId(), password, newPassword);

    // when
    memberService.changePasswordProcess(command);

    // then
    assertThat(passwordEncoder.matches(newPassword, member.getPassword())).isTrue();
  }

  @Test
  @DisplayName("비밀번호 변경 - 실패 테스트(기존 비밀번호 불일치)")
  void 기존_비밀번호가_일치하지_않으면_비밀번호를_변경할_수_없다() {
    // given
    String password = "password";
    String wrongOldPassword = "wrong-password";
    String newPassword = "new-password";

    Member member = encryptPasswordMember(passwordEncoder.encode(password));
    memberRepository.save(member);

    ChangePasswordCommand command = customChangePasswordCommand(member.getId(), wrongOldPassword, newPassword);

    // when & then
    assertThatThrownBy(() -> memberService.changePasswordProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.INVALID_OLD_PASSWORD.getMessage());
  }

  @Test
  @DisplayName("회원 프로필 이미지 변경 - 성공 테스트")
  void 프로필_이미지_경로를_변경한다() {
    // given
    Member member = defaultMember();
    memberRepository.save(member);
    ChangeProfileImageCommand command = customChangeProfileImageUrlCommand(member.getId());

    // when
    memberService.changeMemberProfileImageProcess(command);

    // then
    assertThat(member.getProfileImageUrl()).isEqualTo(command.fileName());
  }

  @Test
  @DisplayName("회원 삭제 - 성공 테스트(소프트 삭제 + 이벤트 발행 + 쿠키 제거)")
  void 회원을_삭제상태로_변경하고_이벤트를_발행하며_쿠키를_제거한다() {
    // given
    Member member = defaultMember();
    memberRepository.save(member);

    MockHttpServletResponse response = new MockHttpServletResponse();
    RemoveMemberCommand command = new RemoveMemberCommand(member.getId());

    // when
    memberService.softRemoveMemberProcess(command, response);

    // then
    Member after = memberRepository.findById(member.getId()).orElseThrow();
    assertThat(after.isRemove()).isTrue();
    List<String> setCookies = response.getHeaders("Set-Cookie");
    assertThat(setCookies).anySatisfy(h ->
        assertThat(h).contains("AUTHORIZATION=").contains("Max-Age=0"));
    assertThat(setCookies).anySatisfy(h ->
        assertThat(h).contains("REFRESH_KEY=").contains("Max-Age=0"));
  }
}

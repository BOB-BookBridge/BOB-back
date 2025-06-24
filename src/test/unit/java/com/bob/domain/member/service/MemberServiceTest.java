package com.bob.domain.member.service;

import static com.bob.global.exception.response.ApplicationError.ALREADY_EXISTS_EMAIL;
import static com.bob.global.exception.response.ApplicationError.INVALID_OLD_PASSWORD;
import static com.bob.global.exception.response.ApplicationError.IS_SAME_REQUEST;
import static com.bob.global.exception.response.ApplicationError.NOT_EXISTS_MEMBER;
import static com.bob.global.exception.response.ApplicationError.UNVERIFIED_EMAIL;
import static com.bob.support.fixture.command.ChangeProfileCommandFixture.defaultChangeProfileCommand;
import static com.bob.support.fixture.command.ChangeProfileCommandFixture.sameNicknameChangeProfileCommand;
import static com.bob.support.fixture.command.MemberCommandFixture.defaultChangePasswordCommand;
import static com.bob.support.fixture.command.MemberCommandFixture.defaultCreateMemberCommand;
import static com.bob.support.fixture.command.MemberCommandFixture.defaultIssuePasswordCommand;
import static com.bob.support.fixture.domain.EmdAreaFixture.EMD_AREA_ID;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.MemberFixture.defaultIdMember;
import static com.bob.support.fixture.domain.MemberFixture.defaultMember;
import static com.bob.support.fixture.query.MemberQueryFixture.defaultReadProfileQuery;
import static com.bob.support.fixture.response.MemberAreaSummaryResponseFixture.DEFAULT_AREA_SUMMARY_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.bob.domain.member.entity.Member;
import com.bob.domain.member.repository.MemberRepository;
import com.bob.domain.member.service.dto.command.ChangePasswordCommand;
import com.bob.domain.member.service.dto.command.ChangeProfileCommand;
import com.bob.domain.member.service.dto.command.CreateMemberCommand;
import com.bob.domain.member.service.dto.command.IssuePasswordCommand;
import com.bob.domain.member.service.dto.query.ReadProfileQuery;
import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import com.bob.domain.member.service.port.out.MemberAreaPort;
import com.bob.domain.member.service.port.out.MemberMailPort;
import com.bob.domain.member.service.port.out.MemberRedisPort;
import com.bob.domain.member.service.reader.MemberReader;
import com.bob.global.exception.exceptions.ApplicationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@DisplayName("사용자 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

  @InjectMocks
  private MemberService memberService;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private MemberReader memberReader;

  @Mock
  private MemberMailPort mailPort;

  @Mock
  private MemberRedisPort redisPort;

  @Mock
  private MemberAreaPort areaPort;

  @Mock
  private BCryptPasswordEncoder encoder;

  @Test
  @DisplayName("회원가입 - 성공 테스트")
  void 회원가입을_진행할_수_있다() {
    // given
    CreateMemberCommand command = defaultCreateMemberCommand();
    String encodedPassword = "$2a$10";
    given(encoder.encode(command.password())).willReturn(encodedPassword);
    given(redisPort.isVerified(command.email())).willReturn(true);
    ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

    // when
    memberService.signupProcess(command);

    // then
    then(redisPort).should().isVerified(command.email());
    then(redisPort).should().deleteVerified(command.email());
    then(memberRepository).should(times(1)).save(captor.capture());
    Member member = captor.getValue();
    then(areaPort).should().createMemberActivityArea(member.getId(), command.emdId());
    assertThat(member.getEmail()).isEqualTo(command.email());
    assertThat(member.getNickname()).isEqualTo(command.nickname());
    assertThat(member.getPassword()).isEqualTo(encodedPassword);
  }

  @Test
  @DisplayName("회원가입 - 실패 테스트(이메일 인증 X)")
  void 이메일_인증이_되지_않으면_회원가입에_실패한다() {
    // given
    CreateMemberCommand command = defaultCreateMemberCommand();
    given(redisPort.isVerified(command.email())).willReturn(false);

    // when & then
    assertThatThrownBy(() -> memberService.signupProcess(command))
        .isInstanceOf(RuntimeException.class)
        .hasMessage(UNVERIFIED_EMAIL.getMessage());
  }

  @Test
  @DisplayName("회원가입 - 실패 테스트(이미 존재하는 이메일 계정)")
  void 이메일_계정이_존재하면_회원가입에_실패한다() {
    // given
    CreateMemberCommand command = defaultCreateMemberCommand();
    given(memberRepository.existsByEmail(command.email())).willReturn(true);

    // when & then
    assertThatThrownBy(() -> memberService.signupProcess(command))
        .isInstanceOf(RuntimeException.class)
        .hasMessage(ALREADY_EXISTS_EMAIL.getMessage());
  }

  @Test
  @DisplayName("사용자 프로필 조회 - 성공 테스트")
  void 사용자의_프로필을_조회할_수_있다() {
    // given
    Member member = defaultMember();
    ReadProfileQuery query = defaultReadProfileQuery();

    given(memberReader.readMemberById(query.memberId())).willReturn(member);
    given(areaPort.readMemberAreaSummary(MEMBER_ID)).willReturn(DEFAULT_AREA_SUMMARY_RESPONSE);

    // when
    MemberProfileResponse response = memberService.readProfileProcess(query);

    // then
    then(memberReader).should(times(1)).readMemberById(query.memberId());
    assertThat(response.memberId()).isEqualTo(member.getId());
    assertThat(response.nickname()).isEqualTo(member.getNickname());
    assertThat(response.area().emdId()).isEqualTo(EMD_AREA_ID);
    assertThat(response.area().isAuthentication()).isTrue();
  }

  @Test
  @DisplayName("프로필 변경 - 성공 테스트")
  void 닉네임이_다르면_프로필을_변경할_수_있다() {
    // given
    Member member = defaultIdMember();
    ChangeProfileCommand command = defaultChangeProfileCommand(member.getId());

    given(memberReader.readMemberById(member.getId())).willReturn(member);

    // when
    memberService.changeProfileProcess(command);

    // then
    then(memberReader).should().readMemberById(member.getId());
    assertThat(member.getNickname()).isEqualTo(command.nickname());
  }

  @Test
  @DisplayName("프로필 변경 - 실패 테스트(동일한 닉네임)")
  void 닉네임이_동일하면_프로필_변경에_실패한다() {
    // given
    Member member = defaultIdMember();
    ChangeProfileCommand command = sameNicknameChangeProfileCommand(member.getId());

    given(memberReader.readMemberById(member.getId())).willReturn(member);

    // when & then
    assertThatThrownBy(() -> memberService.changeProfileProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(IS_SAME_REQUEST.getMessage());
  }

  @Test
  @DisplayName("임시 비밀번호 발급 - 성공 테스트")
  void 임시_비밀번호_발급을_할_수_있다() {
    // given
    Member member = defaultMember();
    String encodedTempPassword = "$2a$encodedTemp";
    IssuePasswordCommand command = defaultIssuePasswordCommand();
    given(memberReader.readMemberByEmail(member.getEmail())).willReturn(member);
    given(encoder.encode(anyString())).willReturn(encodedTempPassword);

    // when
    memberService.issueTempPasswordProcess(command);

    // then
    then(memberReader).should().readMemberByEmail(member.getEmail());
    then(mailPort).should(times(1)).sendTempPassword(anyString(), anyString());
    then(encoder).should(times(1)).encode(anyString());
    assertThat(member.getPassword()).isEqualTo(encodedTempPassword);
  }

  @Test
  @DisplayName("임시 비밀번호 발급 - 실패 테스트(존재하지 않는 이메일)")
  void 이메일_계정이_존재하지_않으면_임시_비밀번호_발급에_실패한다() {
    // given
    IssuePasswordCommand command = defaultIssuePasswordCommand();
    given(memberReader.readMemberByEmail(command.email())).willThrow(new ApplicationException(NOT_EXISTS_MEMBER));

    // when & then
    assertThatThrownBy(() -> memberService.issueTempPasswordProcess(command))
        .isInstanceOf(RuntimeException.class)
        .hasMessage(NOT_EXISTS_MEMBER.getMessage());
  }

  @Test
  @DisplayName("비밀번호 변경 - 성공 테스트")
  void 기존_비밀번호가_일치하면_비밀번호를_변경할_수_있다() {
    // given
    Member member = defaultIdMember();
    String newPassword = "new-password";
    String encodedNewPassword = "$2a$encodedNew";

    ChangePasswordCommand command = defaultChangePasswordCommand(newPassword);

    given(memberReader.readMemberById(member.getId())).willReturn(member);
    given(encoder.matches(anyString(), anyString())).willReturn(true);
    given(encoder.encode(command.newPassword())).willReturn(encodedNewPassword);

    // when
    memberService.changePasswordProcess(command);

    // then
    then(memberReader).should().readMemberById(member.getId());
    then(encoder).should().matches(defaultMember().getPassword(), command.oldPassword());
    then(encoder).should().encode(newPassword);
    assertThat(member.getPassword()).isEqualTo(encodedNewPassword);
  }

  @Test
  @DisplayName("비밀번호 변경 - 실패 테스트(기존 비밀번호 불일치)")
  void 기존_비밀번호가_일치하지_않으면_비밀번호_변경에_실패한다() {
    // given
    Member member = defaultIdMember();
    String newPassword = "new-password";

    ChangePasswordCommand command = defaultChangePasswordCommand(newPassword);

    given(memberReader.readMemberById(member.getId())).willReturn(member);
    given(encoder.matches(command.oldPassword(), member.getPassword())).willReturn(false);

    // when & then
    assertThatThrownBy(() -> memberService.changePasswordProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(INVALID_OLD_PASSWORD.getMessage());
  }
}

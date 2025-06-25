package com.bob.domain.member.service;

import static com.bob.global.exception.response.ApplicationError.ALREADY_EXISTS_EMAIL;
import static com.bob.global.exception.response.ApplicationError.INVALID_OLD_PASSWORD;
import static com.bob.global.exception.response.ApplicationError.IS_SAME_REQUEST;
import static com.bob.global.exception.response.ApplicationError.UNVERIFIED_EMAIL;
import static com.bob.global.utils.random.RandomUtils.generateCode;

import com.bob.domain.member.entity.Member;
import com.bob.domain.member.repository.MemberRepository;
import com.bob.domain.member.service.dto.command.ChangePasswordCommand;
import com.bob.domain.member.service.dto.command.ChangeProfileCommand;
import com.bob.domain.member.service.dto.command.ChangeProfileImageCommand;
import com.bob.domain.member.service.dto.command.CreateMemberCommand;
import com.bob.domain.member.service.dto.command.IssuePasswordCommand;
import com.bob.domain.member.service.dto.query.ReadProfileQuery;
import com.bob.domain.member.service.dto.response.MemberAreaSummaryResponse;
import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import com.bob.domain.member.service.port.out.MemberAreaPort;
import com.bob.domain.member.service.port.out.MemberMailPort;
import com.bob.domain.member.service.port.out.MemberRedisPort;
import com.bob.domain.member.service.reader.MemberReader;
import com.bob.domain.member.usecase.MemberModifyUseCase;
import com.bob.domain.member.usecase.MemberReadUseCase;
import com.bob.domain.member.usecase.MemberWriteUseCase;
import com.bob.global.exception.exceptions.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService implements MemberWriteUseCase, MemberReadUseCase, MemberModifyUseCase {

  private final MemberRepository memberRepository;
  private final MemberReader memberReader;

  private final MemberAreaPort areaPort;
  private final MemberMailPort mailPort;
  private final MemberRedisPort redisPort;

  private final PasswordEncoder encoder;

  @Transactional
  public void signupProcess(CreateMemberCommand command) {
    verifyEmail(command.email());
    String encodedPassword = encoder.encode(command.password());
    Member member = command.toMember(encodedPassword);
    memberRepository.save(member);
    areaPort.createMemberActivityArea(member.getId(), command.emdId());
  }

  private void verifyEmail(String email) {
    validateEmailNotExists(email);
    validateEmailIsVerified(email);
    redisPort.deleteVerified(email);
  }

  private void validateEmailNotExists(String email) {
    if (memberRepository.existsByEmail(email)) {
      throw new ApplicationException(ALREADY_EXISTS_EMAIL);
    }
  }

  private void validateEmailIsVerified(String email) {
    if (!redisPort.isVerified(email)) {
      throw new ApplicationException(UNVERIFIED_EMAIL);
    }
  }

  @Transactional(readOnly = true)
  public MemberProfileResponse readProfileProcess(ReadProfileQuery query) {
    Member member = memberReader.readMemberById(query.memberId());
    MemberAreaSummaryResponse areaSummary = areaPort.readMemberAreaSummary(query.memberId());
    return MemberProfileResponse.from(member, areaSummary);
  }

  @Transactional
  public void changeProfileProcess(ChangeProfileCommand command) {
    Member member = memberReader.readMemberById(command.memberId());
    verifyNickname(member, command.nickname());
    member.updateNickname(command.nickname());
  }

  private void verifyNickname(Member member, String nickname) {
    if (member.isEqualsNickname(nickname)) {
      throw new ApplicationException(IS_SAME_REQUEST);
    }
  }

  @Transactional
  public void changePasswordProcess(ChangePasswordCommand command) {
    Member member = memberReader.readMemberById(command.memberId());
    verifyPassword(member.getPassword(), command.oldPassword());
    member.updatePassword(encoder.encode(command.newPassword()));
  }

  private void verifyPassword(String memberPassword, String receiveOldPassword) {
    if (!encoder.matches(receiveOldPassword, memberPassword)) {
      throw new ApplicationException(INVALID_OLD_PASSWORD);
    }
  }

  @Transactional
  public void issueTempPasswordProcess(IssuePasswordCommand command) {
    Member member = memberReader.readMemberByEmail(command.email());
    String tempPassword = generateCode(12);
    mailPort.sendTempPassword(command.email(), tempPassword);
    member.updatePassword(encoder.encode(tempPassword));
  }

  @Transactional
  public void changeMemberProfileImageProcess(ChangeProfileImageCommand command) {
    Member member = memberReader.readMemberById(command.memberId());
    member.updateProfileImageUrl(command.fileName());
  }
}

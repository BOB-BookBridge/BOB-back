package com.bob.domain.member.usecase;

import com.bob.domain.member.service.dto.command.ChangePasswordCommand;
import com.bob.domain.member.service.dto.command.ChangeProfileCommand;
import com.bob.domain.member.service.dto.command.ChangeProfileImageCommand;
import com.bob.domain.member.service.dto.command.IssuePasswordCommand;

public interface MemberModifyUseCase {

  void changePasswordProcess(ChangePasswordCommand command);

  void changeProfileProcess(ChangeProfileCommand command);

  void issueTempPasswordProcess(IssuePasswordCommand command);

  void changeMemberProfileImageProcess(ChangeProfileImageCommand command);
}

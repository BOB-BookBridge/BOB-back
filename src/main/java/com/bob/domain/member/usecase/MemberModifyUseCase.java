package com.bob.domain.member.usecase;

import com.bob.domain.member.service.dto.command.ChangePasswordCommand;
import com.bob.domain.member.service.dto.command.ChangeProfileCommand;
import com.bob.domain.member.service.dto.command.ChangeProfileImageUrlCommand;
import com.bob.domain.member.service.dto.command.IssuePasswordCommand;
import com.bob.domain.member.service.dto.response.MemberProfileImageUrlResponse;

public interface MemberModifyUseCase {

  void changePasswordProcess(ChangePasswordCommand command);

  void changeProfileProcess(ChangeProfileCommand command);

  void issueTempPasswordProcess(IssuePasswordCommand command);

  MemberProfileImageUrlResponse changeProfileImageUrlProcess(ChangeProfileImageUrlCommand command);
}

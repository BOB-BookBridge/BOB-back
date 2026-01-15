package com.bob.core.member.application.port.in;

import java.util.UUID;

import jakarta.servlet.http.HttpServletResponse;

import com.bob.core.member.application.dto.command.ChangePasswordCommand;
import com.bob.core.member.application.dto.command.ChangeProfileCommand;
import com.bob.core.member.application.dto.command.ChangeProfileImageCommand;
import com.bob.core.member.application.dto.command.ChangeStatusCommand;
import com.bob.core.member.application.dto.command.UpdateMemoCommand;
import com.bob.core.member.domain.Member;

public interface MemberModifier {

    Member changeStatus(UUID memberId, ChangeStatusCommand command);

    Member changePassword(UUID memberId, ChangePasswordCommand command);

    Member changeProfile(UUID memberId, ChangeProfileCommand command);

    Member changeProfileImage(UUID memberId, ChangeProfileImageCommand command);

    Member issueTempPassword(String email);

    Member activate(String email);

    Member deactivate(UUID memberId, HttpServletResponse response);

    Member updateMemoForAdmin(UUID memberId, UpdateMemoCommand command);
}

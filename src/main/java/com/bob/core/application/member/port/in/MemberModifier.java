package com.bob.core.application.member.port.in;

import java.util.UUID;

import jakarta.servlet.http.HttpServletResponse;

import com.bob.core.application.member.dto.command.ChangePasswordCommand;
import com.bob.core.application.member.dto.command.ChangeProfileCommand;
import com.bob.core.application.member.dto.command.ChangeProfileImageCommand;
import com.bob.core.domain.member.Member;

public interface MemberModifier {

    Member changePassword(UUID memberId, ChangePasswordCommand command);

    Member changeProfile(UUID memberId, ChangeProfileCommand command);

    Member changeProfileImage(UUID memberId, ChangeProfileImageCommand command);

    Member issueTempPassword(String email);

    Member activate(String email);

    Member deactivate(UUID memberId, HttpServletResponse response);
}

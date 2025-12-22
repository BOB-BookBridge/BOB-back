package com.bob.admin.member.application.port.in;

import com.bob.admin.member.application.dto.command.ChangeManagementMemberStatusCommand;
import com.bob.admin.member.application.port.result.ManagementMember;

public interface ManagementMemberModifier {

    ManagementMember changeStatus(ChangeManagementMemberStatusCommand command);
}

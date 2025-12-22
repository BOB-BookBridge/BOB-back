package com.bob.admin.application.port.in;

import com.bob.admin.application.dto.command.ChangeManagementMemberStatusCommand;
import com.bob.admin.application.port.result.ManagementMember;

public interface ManagementMemberModifier {

    ManagementMember changeStatus(ChangeManagementMemberStatusCommand command);
}

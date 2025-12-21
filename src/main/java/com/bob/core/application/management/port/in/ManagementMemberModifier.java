package com.bob.core.application.management.port.in;

import com.bob.core.application.management.dto.command.ChangeManagementMemberStatusCommand;
import com.bob.core.application.management.port.result.ManagementMember;

public interface ManagementMemberModifier {

    ManagementMember changeStatus(ChangeManagementMemberStatusCommand command);
}

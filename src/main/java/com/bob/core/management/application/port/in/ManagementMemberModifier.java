package com.bob.core.management.application.port.in;

import com.bob.core.management.application.dto.command.ChangeManagementMemberStatusCommand;
import com.bob.core.management.application.port.result.ManagementMember;

public interface ManagementMemberModifier {

    ManagementMember changeStatus(ChangeManagementMemberStatusCommand command);
}

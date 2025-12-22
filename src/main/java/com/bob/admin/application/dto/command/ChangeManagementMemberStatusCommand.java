package com.bob.admin.application.dto.command;

import java.util.UUID;

public record ChangeManagementMemberStatusCommand(UUID memberId, String status, String memo) {

}

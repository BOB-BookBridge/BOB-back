package com.bob.core.application.management.dto.command;

import java.util.UUID;

public record ChangeManagementMemberStatusCommand(UUID memberId, String status, String memo) {

}

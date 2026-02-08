package com.bob.admin.post.application.dto.command;

import java.util.UUID;

public record ProcessManagementPostStatusCommand(UUID managerId, String status, String memo) {

}

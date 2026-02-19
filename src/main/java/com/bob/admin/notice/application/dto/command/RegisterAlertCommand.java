package com.bob.admin.notice.application.dto.command;

import java.util.UUID;

public record RegisterAlertCommand(UUID writerId, String title, String content) {

}

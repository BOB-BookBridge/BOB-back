package com.bob.domain.member.service.dto.command;

import java.util.UUID;

public record ChangeProfileImageCommand(
    UUID memberId,
    String fileName
) {

}

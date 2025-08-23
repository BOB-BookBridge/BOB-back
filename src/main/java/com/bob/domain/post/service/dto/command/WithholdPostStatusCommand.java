package com.bob.domain.post.service.dto.command;

import java.util.UUID;

public record WithholdPostStatusCommand(
    UUID memberId
) {

}

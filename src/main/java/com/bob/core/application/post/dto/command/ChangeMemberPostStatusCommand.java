package com.bob.core.application.post.dto.command;

import java.util.UUID;

import com.bob.core.domain.post.status.Status;

public record ChangeMemberPostStatusCommand(UUID memberId, Status status) {

}

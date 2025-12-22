package com.bob.core.post.application.dto.command;

import java.util.UUID;

import com.bob.core.post.domain.status.Status;

public record ChangeMemberPostStatusCommand(UUID memberId, Status status) {

}

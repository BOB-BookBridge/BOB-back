package com.bob.core.post.application.dto.query;

import java.util.UUID;

public record ReadPostDetailQuery(UUID memberId, boolean isClient) {

}

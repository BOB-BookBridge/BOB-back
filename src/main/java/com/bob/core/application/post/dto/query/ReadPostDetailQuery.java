package com.bob.core.application.post.dto.query;

import java.util.UUID;

public record ReadPostDetailQuery(UUID memberId, boolean isClient) {

}

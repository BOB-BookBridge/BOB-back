package com.bob.core.application.chat.dto.query;

import java.util.UUID;

public record ReadChatroomByPostAndMemberQuery(Long postId, UUID buyerId) {

}

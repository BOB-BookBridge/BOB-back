package com.bob.core.chat.application.dto.query;

import java.util.UUID;

public record ReadChatroomByPostAndMemberQuery(Long postId, UUID buyerId) {

}

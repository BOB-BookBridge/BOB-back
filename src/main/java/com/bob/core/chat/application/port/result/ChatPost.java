package com.bob.core.chat.application.port.result;

import java.util.UUID;

import lombok.Builder;

@Builder
public record ChatPost(Long id, String tradeStatus, UUID sellerId, String title, String thumbnailUrl, int sellPrice) {

}

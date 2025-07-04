package com.bob.domain.chat.service.port.out;

import com.bob.domain.post.service.dto.response.PostDetailResponse;

public interface ChatPostPort {

  PostDetailResponse readChatPostSummary(Long postId);
}

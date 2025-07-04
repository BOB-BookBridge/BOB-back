package com.bob.web.post.adapter.in;

import com.bob.domain.chat.service.port.out.ChatPostPort;
import com.bob.domain.post.service.dto.query.ReadPostDetailQuery;
import com.bob.domain.post.service.dto.response.PostDetailResponse;
import com.bob.domain.post.usecase.PostReadUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChatPostAdapter implements ChatPostPort {

  private final PostReadUseCase readUseCase;

  @Override
  public PostDetailResponse readChatPostSummary(Long postId) {
    return readUseCase.readPostDetailProcess(ReadPostDetailQuery.of(null, postId, false));
  }
}

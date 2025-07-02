package com.bob.web.post.adapter.in;

import com.bob.domain.chat.service.dto.response.ChatPostResponse;
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
  public ChatPostResponse readChatPostSummary(Long postId) {
    PostDetailResponse detail = readUseCase.readPostDetailProcess(ReadPostDetailQuery.of(null, postId));
    return ChatPostResponse.of(detail.postId(), detail.sellerId(), detail.book().title(), detail.thumbnailUrl());
  }
}

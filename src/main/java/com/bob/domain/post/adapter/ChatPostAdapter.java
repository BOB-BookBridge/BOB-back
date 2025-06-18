package com.bob.domain.post.adapter;

import com.bob.domain.chat.service.dto.response.ChatPostResponse;
import com.bob.domain.chat.service.port.out.ChatPostPort;
import com.bob.domain.post.service.PostService;
import com.bob.domain.post.service.dto.query.ReadPostDetailQuery;
import com.bob.domain.post.service.dto.response.PostDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChatPostAdapter implements ChatPostPort {

  private final PostService postService;

  @Override
  public ChatPostResponse readChatPostSummary(Long postId) {
    PostDetailResponse detail = postService.readPostDetailProcess(ReadPostDetailQuery.of(null, postId));
    return ChatPostResponse.of(detail.postId(), detail.sellerId(), detail.book().title());
  }
}

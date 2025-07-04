package com.bob.domain.post.adapter;

import static com.bob.support.fixture.response.PostResponseFixture.DEFAULT_POST_DETAIL_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.bob.web.post.adapter.in.ChatPostAdapter;
import com.bob.domain.post.service.PostService;
import com.bob.domain.post.service.dto.response.PostDetailResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("게시글 제공자 테스트")
@ExtendWith(MockitoExtension.class)
class PostAdapterTest {

  @InjectMocks
  private ChatPostAdapter postAdapter;

  @Mock
  private PostService postService;

  @Test
  @DisplayName("채팅방 생성 시 사용하는 게시글 정보 조회 테스트")
  void postId로_게시글_요약_정보를_조회할_수_있다() {
    // given
    Long postId = 1L;
    PostDetailResponse expect = DEFAULT_POST_DETAIL_RESPONSE(postId);
    given(postService.readPostDetailProcess(any())).willReturn(expect);

    // when
    PostDetailResponse response = postAdapter.readChatPostSummary(postId);

    // then
    assertThat(response.postId()).isEqualTo(expect.postId());
    assertThat(response.sellerId()).isEqualTo(expect.sellerId());
    assertThat(response.book().title()).isEqualTo(expect.book().title());
    then(postService).should(times(1)).readPostDetailProcess(any());
  }
}
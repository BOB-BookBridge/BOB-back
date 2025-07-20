package com.bob.web.post.adapter.in;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.response.PostResponseFixture.DEFAULT_POST_DETAIL_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.bob.domain.post.service.dto.query.ReadPostDetailQuery;
import com.bob.domain.post.service.dto.response.PostDetailResponse;
import com.bob.domain.post.usecase.PostReadUseCase;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("TradePostAdapterTest 테스트")
class TradePostAdapterTest {

  @InjectMocks
  private TradePostAdapter tradePostAdapter;

  @Mock
  private PostReadUseCase readUseCase;

  @Test
  @DisplayName("게시글 ID를 통한 게시글 작성자 ID 조회 호출 테스트")
  void 게시글_작성자의_ID를_조회한다() {
    // given
    Long postId = 1L;
    PostDetailResponse expect = DEFAULT_POST_DETAIL_RESPONSE(postId);
    given(readUseCase.readPostDetailProcess(any(ReadPostDetailQuery.class))).willReturn(expect);

    // when
    UUID result = tradePostAdapter.readTradePostOwnerId(postId);

    // then
    assertThat(result).isEqualTo(MEMBER_ID);
  }
}

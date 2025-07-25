package com.bob.web.post.adapter.in;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.response.PostResponseFixture.DEFAULT_POST_DETAIL_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.domain.post.service.dto.command.ChangePostStatusCommand;
import com.bob.domain.post.service.dto.query.ReadPostDetailQuery;
import com.bob.domain.post.service.dto.response.PostDetailResponse;
import com.bob.domain.post.usecase.PostModifyUseCase;
import com.bob.domain.post.usecase.PostReadUseCase;
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

  @Mock
  private PostModifyUseCase modifyUseCase;

  @Test
  @DisplayName("게시글 ID를 통한 게시글 상세 조회 기능 호출 테스트")
  void 게시글의_상세_정보를_조회한다() {
    // given
    Long postId = 1L;
    PostDetailResponse expect = DEFAULT_POST_DETAIL_RESPONSE(postId);
    given(readUseCase.readPostDetailProcess(any(ReadPostDetailQuery.class))).willReturn(expect);

    // when
    PostDetailResponse result = tradePostAdapter.readTradePostSummary(postId);

    // then
    assertThat(result.sellerId()).isEqualTo(MEMBER_ID);
  }

  @Test
  @DisplayName("게시글 상태 변경 기능 호출 테스트")
  void 게시글_상태를_변경할_수_있다() {
    // given
    Long postId = 1L;
    String status = "COMPLETED";

    // when
    tradePostAdapter.changePostStatus(postId, status);

    // then
    ChangePostStatusCommand expectedCommand = new ChangePostStatusCommand(postId, status);
    then(modifyUseCase).should().changePostStatusProcess(expectedCommand);
  }
}

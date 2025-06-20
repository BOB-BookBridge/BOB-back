package com.bob.domain.post.service.reader;

import static com.bob.global.exception.response.ApplicationError.INVALID_POST_FAVORITE;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.PostFavoriteFixture.DEFAULT_MOCK_POST_FAVORITES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.domain.post.entity.Post;
import com.bob.domain.post.entity.PostFavorite;
import com.bob.domain.post.repository.PostFavoriteRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostFavoriteReader 테스트")
class PostFavoriteReaderTest {

  @InjectMocks
  private PostFavoriteReader postFavoriteReader;

  @Mock
  private PostFavoriteRepository postFavoriteRepository;

  @Mock
  private Post post;

  private Pageable pageable = PageRequest.of(0, 12);

  @Test
  @DisplayName("좋아요 게시글 조회 - 성공 테스트")
  void 좋아요가_존재하면_PostFavorite를_반환한다() {
    // given
    PostFavorite postFavorite = PostFavorite.create(MEMBER_ID, post);
    given(postFavoriteRepository.findByMemberIdAndPostId(MEMBER_ID, post.getId())).willReturn(Optional.of(postFavorite));

    // when
    PostFavorite result = postFavoriteReader.readFavoritePostByMemberIdAndPostId(MEMBER_ID, post.getId());

    // then
    assertNotNull(result);
    then(postFavoriteRepository).should().findByMemberIdAndPostId(MEMBER_ID, post.getId());
  }

  @Test
  @DisplayName("좋아요 게시글 조회 - 실패 테스트(존재하지 않는 좋아요 게시글)")
  void 좋아요가_없으면_ApplicationException이_발생한다() {
    // given
    given(postFavoriteRepository.findByMemberIdAndPostId(MEMBER_ID, post.getId())).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> postFavoriteReader.readFavoritePostByMemberIdAndPostId(MEMBER_ID, post.getId()))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(INVALID_POST_FAVORITE.getMessage());
  }

  @Test
  @DisplayName("좋아요 게시글 목록 조회 테스트")
  void 사용자가_좋아요_한_게시글_목록을_조회한다() {
    // given
    given(postFavoriteRepository.findByMemberIdOrderByCreatedAtDesc(MEMBER_ID, pageable)).willReturn(
        DEFAULT_MOCK_POST_FAVORITES());

    // when
    List<PostFavorite> result = postFavoriteReader.readFavoritePostsByMemberId(MEMBER_ID, pageable);

    // then
    then(postFavoriteRepository).should().findByMemberIdOrderByCreatedAtDesc(MEMBER_ID, pageable);
    assertThat(result).hasSize(2);
  }
}
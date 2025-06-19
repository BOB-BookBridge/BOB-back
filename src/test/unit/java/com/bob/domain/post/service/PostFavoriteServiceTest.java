package com.bob.domain.post.service;

import static com.bob.global.exception.response.ApplicationError.ALREADY_POST_FAVORITE;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.PostFavoriteFixture.DEFAULT_MOCK_POST_FAVORITES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;

import com.bob.domain.post.entity.Post;
import com.bob.domain.post.entity.PostFavorite;
import com.bob.domain.post.repository.PostFavoriteRepository;
import com.bob.domain.post.service.dto.response.PostFavoritesResponse;
import com.bob.domain.post.service.reader.PostFavoriteReader;
import com.bob.global.exception.exceptions.ApplicationException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostFavoriteService 테스트")
class PostFavoriteServiceTest {

  @InjectMocks
  private PostFavoriteService postFavoriteService;

  @Mock
  private PostFavoriteRepository postFavoriteRepository;

  @Mock
  private PostFavoriteReader postFavoriteReader;

  @Mock
  private Post post;

  private Pageable pageable = PageRequest.of(0, 12);

  @Test
  @DisplayName("게시글 즐겨찾기 등록 - 성공 테스트")
  void 게시글_즐겨찾기_등록에_성공한다() {
    // given
    PostFavorite postFavorite = PostFavorite.create(MEMBER_ID, post);
    given(postFavoriteRepository.save(any(PostFavorite.class))).willReturn(postFavorite);

    // when
    postFavoriteService.createPostFavoriteProcess(MEMBER_ID, post);

    // then
    then(postFavoriteRepository).should().save(any(PostFavorite.class));
  }

  @Test
  @DisplayName("게시글 즐겨찾기 등록 - 실패 테스트 (중복 즐겨찾기)")
  void 이미_즐겨찾기한_게시글이면_예외가_발생한다() {
    // given
    willThrow(new DataIntegrityViolationException("duplicate"))
        .given(postFavoriteRepository)
        .save(any(PostFavorite.class));

    // when & then
    assertThatThrownBy(() -> postFavoriteService.createPostFavoriteProcess(MEMBER_ID, post))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ALREADY_POST_FAVORITE.getMessage());

    then(postFavoriteRepository).should().save(any(PostFavorite.class));
  }

  @Test
  @DisplayName("좋아요 해제 - 성공 테스트")
  void 좋아요를_성공적으로_해제한다() {
    // given
    PostFavorite postFavorite = PostFavorite.create(MEMBER_ID, post);
    given(postFavoriteReader.readFavoritePostByMemberIdAndPostId(MEMBER_ID, post.getId())).willReturn(postFavorite);

    // when
    postFavoriteService.deletePostFavoriteProcess(MEMBER_ID, post.getId());

    // then
    then(postFavoriteReader)
        .should(times(1))
        .readFavoritePostByMemberIdAndPostId(MEMBER_ID, post.getId());

    then(postFavoriteRepository)
        .should(times(1))
        .delete(postFavorite);
  }

  @Test
  @DisplayName("게시글 좋아요 여부 확인 - true 반환")
  void 게시글을_좋아요한_경우_true를_반환한다() {
    // given
    UUID memberId = MEMBER_ID;
    Long postId = 1L;
    given(postFavoriteRepository.existsByMemberIdAndPostId(memberId, postId)).willReturn(true);

    // when
    boolean result = postFavoriteService.isFavorite(memberId, postId);

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("게시글 좋아요 여부 확인 - false 반환")
  void 게시글을_좋아요하지_않은_경우_false를_반환한다() {
    // given
    UUID memberId = MEMBER_ID;
    Long postId = 2L;
    given(postFavoriteRepository.existsByMemberIdAndPostId(memberId, postId)).willReturn(false);

    // when
    boolean result = postFavoriteService.isFavorite(memberId, postId);

    // then
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("좋아요 한 게시글 목록 조회")
  void 사용자의_즐겨찾기_게시글_목록을_조회할_수_있다() {
    // given
    UUID memberId = MEMBER_ID;
    given(postFavoriteReader.readFavoritePostsByMemberId(memberId, pageable)).willReturn(DEFAULT_MOCK_POST_FAVORITES());
    given(postFavoriteRepository.countByMemberId(memberId)).willReturn((long) DEFAULT_MOCK_POST_FAVORITES().size());

    // when
    PostFavoritesResponse response = postFavoriteService.readMemberFavoritePosts(memberId, pageable);

    // then
    assertThat(response.totalCount()).isEqualTo(2L);
    assertThat(response.postFavorites()).hasSize(2);

    then(postFavoriteReader).should().readFavoritePostsByMemberId(memberId, pageable);
    then(postFavoriteRepository).should().countByMemberId(memberId);
  }

  @Test
  @DisplayName("게시글 삭제 시 - 해당 게시글의 모든 좋아요를 삭제한다")
  void 게시글을_삭제하면_해당_게시글의_좋아요가_모두_삭제된다() {
    // given
    Long postId = 1L;

    // when
    postFavoriteService.removePostFavoriteProcess(postId);

    // then
    then(postFavoriteRepository).should(times(1)).deleteAllByPostId(postId);
  }
}
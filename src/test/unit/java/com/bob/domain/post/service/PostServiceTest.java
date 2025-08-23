package com.bob.domain.post.service;

import static com.bob.global.exception.response.ApplicationError.ALREADY_POST_FAVORITE;
import static com.bob.global.exception.response.ApplicationError.INVALID_POST_FAVORITE;
import static com.bob.global.exception.response.ApplicationError.NOT_VERIFIED_MEMBER;
import static com.bob.support.fixture.command.ChangePostCommandFixture.DEFAULT_CHANGE_POST_COMMAND;
import static com.bob.support.fixture.command.CreatePostCommandFixture.createPostCommandWithImageRefId;
import static com.bob.support.fixture.command.CreatePostCommandFixture.defaultCreatePostCommand;
import static com.bob.support.fixture.command.RegisterPostFavoriteCommandFixture.defaultRegisterPostFavoriteCommand;
import static com.bob.support.fixture.domain.BookFixture.defaultBook;
import static com.bob.support.fixture.domain.CategoryFixture.defaultCategory;
import static com.bob.support.fixture.domain.EmdAreaFixture.EMD_AREA_ID;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.PostFixture.DEFAULT_MOCK_POSTS;
import static com.bob.support.fixture.domain.PostFixture.defaultIdPost;
import static com.bob.support.fixture.domain.PostFixture.defaultPost;
import static com.bob.support.fixture.domain.PostFixture.customStatusPost;
import static com.bob.support.fixture.query.PostQueryFixture.defaultReadFilteredPostsQuery;
import static com.bob.support.fixture.query.PostQueryFixture.defaultReadMemberFavoritePostsQuery;
import static com.bob.support.fixture.query.PostQueryFixture.searchCategoryQuery;
import static com.bob.support.fixture.response.PostAreaSummaryResponseFixture.DEFAULT_POST_AREA_SUMMARY;
import static com.bob.support.fixture.response.PostAreaSummaryResponseFixture.NOT_VALID_POST_AREA_SUMMARY;
import static com.bob.support.fixture.response.PostFileSummaryResponseFixture.DEFAULT_READ_FILES_RESPONSE;
import static com.bob.support.fixture.response.PostMemberSummaryResponseFixture.DEFAULT_MEMBER_SUMMARY;
import static com.bob.support.fixture.response.PostResponseFixture.DEFAULT_FAVORITE_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;

import com.bob.domain.book.entity.Book;
import com.bob.domain.book.service.BookService;
import com.bob.domain.category.entity.Category;
import com.bob.domain.category.service.reader.CategoryReader;
import com.bob.domain.post.entity.Post;
import com.bob.domain.post.entity.status.PostStatus;
import com.bob.domain.post.repository.PostRepository;
import com.bob.domain.post.service.dto.command.ChangePostCommand;
import com.bob.domain.post.service.dto.command.ChangePostStatusCommand;
import com.bob.domain.post.service.dto.command.CreatePostCommand;
import com.bob.domain.post.service.dto.command.RegisterPostFavoriteCommand;
import com.bob.domain.post.service.dto.command.RemovePostCommand;
import com.bob.domain.post.service.dto.query.ReadFilteredPostsQuery;
import com.bob.domain.post.service.dto.query.ReadPostDetailQuery;
import com.bob.domain.post.service.dto.query.ReadPostFavoritesQuery;
import com.bob.domain.post.service.dto.response.PostCreateResponse;
import com.bob.domain.post.service.dto.response.PostDetailResponse;
import com.bob.domain.post.service.dto.response.PostsResponse;
import com.bob.domain.post.service.port.out.PostAreaPort;
import com.bob.domain.post.service.port.out.PostFilePort;
import com.bob.domain.post.service.port.out.PostMemberPort;
import com.bob.domain.post.service.reader.PostReader;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DisplayName("게시글 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

  @InjectMocks
  private PostService postService;

  @Mock
  private PostFavoriteService postFavoriteService;

  @Mock
  private PostReader postReader;

  @Mock
  private PostRepository postRepository;

  @Mock
  private BookService bookService;

  @Mock
  private CategoryReader categoryReader;

  @Mock
  private PostMemberPort memberPort;

  @Mock
  private PostAreaPort areaPort;

  @Mock
  private PostFilePort filePort;

  private Pageable pageable = PageRequest.of(0, 12);

  @Test
  @DisplayName("게시글 등록 - 성공 테스트")
  void 게시글을_등록할_수_있다() {
    // given
    CreatePostCommand command = defaultCreatePostCommand();
    Book book = defaultBook();
    Category category = defaultCategory();
    given(bookService.createBookProcess(command.toBookCreateCommand())).willReturn(book);
    given(categoryReader.readCategoryById(command.categoryId())).willReturn(category);
    given(areaPort.readPostAreaSummary(MEMBER_ID)).willReturn(DEFAULT_POST_AREA_SUMMARY);

    ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);

    // when
    PostCreateResponse response = postService.createPostProcess(command);

    // then
    then(bookService).should().createBookProcess(command.toBookCreateCommand());
    then(categoryReader).should().readCategoryById(command.categoryId());
    then(postRepository).should(times(1)).save(captor.capture());
    then(filePort).should(times(1)).modifyReferenceId(command.fileNames(), String.valueOf(response.postId()));

    Post post = captor.getValue();
    assertThat(post.getBook()).isEqualTo(book);
    assertThat(post.getSellerId()).isEqualTo(MEMBER_ID);
    assertThat(post.getCategory()).isEqualTo(category);
  }

  @DisplayName("게시글 등록 - 이미지 매핑 생략 (null)")
  @Test
  void 게시글_등록시_referenceId가_null이면_이미지_매핑을_하지_않는다() {
    // given
    CreatePostCommand command = createPostCommandWithImageRefId(null);
    given(bookService.createBookProcess(command.toBookCreateCommand())).willReturn(defaultBook());
    given(categoryReader.readCategoryById(command.categoryId())).willReturn(defaultCategory());
    given(areaPort.readPostAreaSummary(MEMBER_ID)).willReturn(DEFAULT_POST_AREA_SUMMARY);

    // when
    postService.createPostProcess(command);

    // then
    then(filePort).shouldHaveNoInteractions();
  }

  @DisplayName("게시글 등록 - 이미지 매핑 생략 (공백)")
  @Test
  void 게시글_등록시_referenceId가_공백이면_이미지_매핑을_하지_않는다() {
    // given
    CreatePostCommand command = createPostCommandWithImageRefId(List.of());
    given(bookService.createBookProcess(command.toBookCreateCommand())).willReturn(defaultBook());
    given(categoryReader.readCategoryById(command.categoryId())).willReturn(defaultCategory());
    given(areaPort.readPostAreaSummary(MEMBER_ID)).willReturn(DEFAULT_POST_AREA_SUMMARY);

    // when
    postService.createPostProcess(command);

    // then
    then(filePort).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("게시글 등록 - 실패 테스트(위치 인증 안된 사용자)")
  void 위치인증_되지_않은_사용자는_게시글을_등록할_수_없다() {
    // given
    CreatePostCommand command = defaultCreatePostCommand();
    given(areaPort.readPostAreaSummary(MEMBER_ID)).willReturn(NOT_VALID_POST_AREA_SUMMARY);

    // when & then
    assertThatThrownBy(() -> postService.createPostProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(NOT_VERIFIED_MEMBER.getMessage());
  }

  @DisplayName("게시글 좋아요 - 성공 테스트")
  @Test
  void 게시글을_좋아요하면_좋아요_count가_증가한다() {
    // given
    RegisterPostFavoriteCommand command = defaultRegisterPostFavoriteCommand();
    Post post = defaultIdPost(defaultCategory(), defaultBook(), MEMBER_ID, EMD_AREA_ID);
    Long postId = command.postId();

    given(postReader.readPostById(postId)).willReturn(post);

    // when
    postService.registerPostFavoriteProcess(command);

    // then
    then(postReader).should().readPostById(postId);
    then(postFavoriteService).should().createPostFavoriteProcess(MEMBER_ID, post);
    then(postRepository).should().increaseFavoriteCount(postId);
  }

  @DisplayName("게시글 좋아요 - 실패 테스트 (이미 좋아요한 게시글)")
  @Test
  void 이미_좋아요한_게시글이면_예외가_발생한다() {
    // given
    RegisterPostFavoriteCommand command = defaultRegisterPostFavoriteCommand();
    Post post = defaultIdPost(defaultCategory(), defaultBook(), MEMBER_ID, EMD_AREA_ID);
    Long postId = command.postId();
    given(postReader.readPostById(postId)).willReturn(post);

    willThrow(new ApplicationException(ALREADY_POST_FAVORITE))
        .given(postFavoriteService).createPostFavoriteProcess(MEMBER_ID, post);

    // when & then
    assertThatThrownBy(() -> postService.registerPostFavoriteProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ALREADY_POST_FAVORITE.getMessage());

    then(postReader).should().readPostById(postId);
    then(postFavoriteService).should().createPostFavoriteProcess(MEMBER_ID, post);
    then(postRepository).shouldHaveNoInteractions();
  }

  @DisplayName("게시글 좋아요 해제 - 성공 테스트")
  @Test
  void 게시글_좋아요_해제를_요청하면_scrapCount가_감소한다() {
    // given
    RegisterPostFavoriteCommand command = defaultRegisterPostFavoriteCommand();

    // when
    postService.unregisterPostFavoriteProcess(command);

    // then
    then(postFavoriteService)
        .should(times(1))
        .deletePostFavoriteProcess(command.memberId(), command.postId());

    then(postRepository)
        .should(times(1))
        .decreaseFavoriteCount(command.postId());
  }

  @DisplayName("게시글 좋아요 해제 - 실패 테스트 (좋아요하지 않은 게시글)")
  @Test
  void 좋아요하지_않은_게시글은_해제할_수_없다() {
    // given
    RegisterPostFavoriteCommand command = defaultRegisterPostFavoriteCommand();

    willThrow(new ApplicationException(INVALID_POST_FAVORITE))
        .given(postFavoriteService)
        .deletePostFavoriteProcess(command.memberId(), command.postId());

    // when & then
    assertThatThrownBy(() -> postService.unregisterPostFavoriteProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining(INVALID_POST_FAVORITE.getMessage());

    then(postFavoriteService)
        .should(times(1))
        .deletePostFavoriteProcess(command.memberId(), command.postId());

    then(postRepository).shouldHaveNoInteractions();
  }

  @DisplayName("게시글 목록 조회 테스트")
  @Test
  void 게시글_목록을_조회할_수_있다() {
    // given
    ReadFilteredPostsQuery query = defaultReadFilteredPostsQuery();
    given(postReader.readFilteredPosts(query, pageable)).willReturn(DEFAULT_MOCK_POSTS());
    given(postRepository.countFilteredPosts(query)).willReturn(2L);

    // when
    PostsResponse response = postService.readFilteredPostsProcess(query, pageable);

    // then
    assertThat(response.totalCount()).isEqualTo(2L);
    then(postReader).should(times(1)).readFilteredPosts(query, pageable);
    then(postRepository).should(times(1)).countFilteredPosts(query);
  }

  @DisplayName("게시글 목록 조회 테스트 - 부모 카테고리 조회")
  @Test
  void 카테고리를_통한_게시글_목록_조회_시_자식_카테고리_게시글을_포함한다() {
    // given
    ReadFilteredPostsQuery query = searchCategoryQuery(); // categoryId = 1, 1의 자식 = 12, 13, 14, 15, 16
    given(categoryReader.readChildCategoryIds(query.categoryIds().get(0))).willReturn(List.of(12, 13, 14, 15, 16));
    given(postReader.readFilteredPosts(query, pageable)).willReturn(DEFAULT_MOCK_POSTS());
    given(postRepository.countFilteredPosts(query)).willReturn(2L);

    // when
    PostsResponse response = postService.readFilteredPostsProcess(query, pageable);

    // then
    then(categoryReader).should(times(1)).readChildCategoryIds(query.categoryIds().get(0));
    assertThat(query.categoryIds()).contains(12, 13, 14, 15, 16); // 서비스 단에서 게시글 목록 조회 전 자식 카테고리를 조회 후 카테고리 조건에 추가

    then(postReader).should(times(1)).readFilteredPosts(query, pageable);
    then(postRepository).should(times(1)).countFilteredPosts(query);
    assertThat(response.totalCount()).isEqualTo(2L);
  }

  @Test
  @DisplayName("좋아요한 게시글 목록 조회 테스트")
  void 좋아요한_게시글_목록을_조회할_수_있다() {
    // given
    UUID memberId = UUID.randomUUID();
    ReadPostFavoritesQuery query = defaultReadMemberFavoritePostsQuery(memberId);
    given(postFavoriteService.readMemberFavoritePosts(memberId, pageable)).willReturn(DEFAULT_FAVORITE_RESPONSE());

    // when
    PostsResponse response = postService.readPostFavoritesProcess(query, pageable);

    // then
    assertThat(response.totalCount()).isEqualTo(2L);
    then(postFavoriteService).should(times(1)).readMemberFavoritePosts(query.memberId(), pageable);
  }

  @DisplayName("게시글 상세 조회 - 작성자 본인")
  @Test
  void 게시글_작성자와_조회자가_같다면_isOwner는_true이다() {
    // given
    Post post = defaultPost(defaultCategory(), defaultBook(), MEMBER_ID, EMD_AREA_ID);
    ReadPostDetailQuery query = new ReadPostDetailQuery(MEMBER_ID, post.getId(), true);
    given(postReader.readPostById(post.getId())).willReturn(post);
    given(memberPort.readPostMemberSummary(MEMBER_ID)).willReturn(DEFAULT_MEMBER_SUMMARY);
    given(filePort.readPostFileSummaries(post.getId())).willReturn(DEFAULT_READ_FILES_RESPONSE);
    willDoNothing().given(postRepository).increaseViewCount(post.getId());

    // when
    PostDetailResponse response = postService.readPostDetailProcess(query);

    // then
    assertThat(response.isOwner()).isTrue();
    assertThat(response.images()).hasSize(3);
    then(postRepository).should(times(1)).increaseViewCount(post.getId());
    then(postFavoriteService).should(times(1)).isFavorite(MEMBER_ID, post.getId());
  }

  @DisplayName("게시글 상세 조회 - 작성자 본인 X")
  @Test
  void 게시글_작성자와_조회자가_다르면_isOwner는_false이다() {
    // given
    Post post = defaultPost(defaultCategory(), defaultBook(), MEMBER_ID, EMD_AREA_ID);
    UUID otherMemberId = UUID.randomUUID();

    ReadPostDetailQuery query = new ReadPostDetailQuery(otherMemberId, post.getId(), true);
    given(postReader.readPostById(post.getId())).willReturn(post);
    given(memberPort.readPostMemberSummary(MEMBER_ID)).willReturn(DEFAULT_MEMBER_SUMMARY);
    given(filePort.readPostFileSummaries(post.getId())).willReturn(DEFAULT_READ_FILES_RESPONSE);
    willDoNothing().given(postRepository).increaseViewCount(post.getId());

    // when
    PostDetailResponse response = postService.readPostDetailProcess(query);

    // then
    assertThat(response.isOwner()).isFalse();
    assertThat(response.images()).hasSize(3);
    then(postRepository).should(times(1)).increaseViewCount(post.getId());
    then(postFavoriteService).should(times(1)).isFavorite(otherMemberId, post.getId());
  }

  @DisplayName("게시글 수정 - 성공 테스트")
  @Test
  void 게시글_작성자는_게시글을_수정할_수_있다() {
    // given
    Post post = defaultPost(defaultCategory(), defaultBook(), MEMBER_ID, EMD_AREA_ID);
    ChangePostCommand command = DEFAULT_CHANGE_POST_COMMAND(MEMBER_ID, post.getId());
    given(postReader.readPostById(post.getId())).willReturn(post);

    // when
    postService.changePostProcess(command);

    // then
    assertThat(post.getSellPrice()).isEqualTo(12000);
    assertThat(post.getDescription()).isEqualTo("상태 좋음");
    then(postReader).should().readPostById(post.getId());
  }

  @DisplayName("게시글 수정 - 실패 테스트 (작성자 X)")
  @Test
  void 작성자가_아닌_사람은_게시글을_수정할_수_없다() {
    // given
    Post post = defaultPost(defaultCategory(), defaultBook(), MEMBER_ID, EMD_AREA_ID);
    UUID otherMemberId = UUID.randomUUID();
    given(postReader.readPostById(post.getId())).willReturn(post);
    ChangePostCommand command = DEFAULT_CHANGE_POST_COMMAND(otherMemberId, post.getId());

    // when & then
    assertThatThrownBy(() -> postService.changePostProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.NOT_POST_OWNER.getMessage());

    then(postReader).should().readPostById(post.getId());
  }

  @DisplayName("게시글 상태 수정 - 성공 테스트")
  @Test
  void 게시글_상태를_수정할_수_있다() {
    // given
    Post post = defaultPost(defaultCategory(), defaultBook(), MEMBER_ID, EMD_AREA_ID);
    ChangePostStatusCommand command = new ChangePostStatusCommand(post.getId(), "IN_PROGRESS");
    given(postReader.readPostById(post.getId())).willReturn(post);

    // when
    postService.changePostStatusProcess(command);

    // then
    assertThat(post.getPostStatus()).isEqualTo(PostStatus.IN_PROGRESS);
    then(postReader).should().readPostById(post.getId());
  }

  @DisplayName("게시글 삭제 - 성공 테스트 (작성자 본인)")
  @Test
  void 게시글_작성자는_게시글을_삭제할_수_있다() {
    // given
    Post post = defaultPost(defaultCategory(), defaultBook(), MEMBER_ID, EMD_AREA_ID);

    RemovePostCommand command = new RemovePostCommand(MEMBER_ID, post.getId());
    given(postReader.readPostById(command.postId())).willReturn(post);

    // when
    postService.removePostProcess(command);

    // then
    then(postReader).should(times(1)).readPostById(post.getId());
    then(postFavoriteService).should(times(1)).removePostFavoriteProcess(post.getId());
    assertThat(post.getPostStatus()).isEqualTo(PostStatus.REMOVED);
  }

  @DisplayName("게시글 삭제 - 실패 테스트 (작성자 X)")
  @Test
  void 작성자가_아니면_게시글을_삭제할_수_없다() {
    // given
    Post post = defaultPost(defaultCategory(), defaultBook(), MEMBER_ID, EMD_AREA_ID);
    UUID otherMemberId = UUID.randomUUID();

    RemovePostCommand command = new RemovePostCommand(otherMemberId, post.getId());

    given(postReader.readPostById(command.postId())).willReturn(post);

    // when & then
    assertThatThrownBy(() -> postService.removePostProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.NOT_POST_OWNER.getMessage());

    then(postReader).should(times(1)).readPostById(post.getId());
    then(postFavoriteService).shouldHaveNoInteractions();
    assertThat(post.getPostStatus()).isEqualTo(PostStatus.READY);
  }

  @DisplayName("게시글 삭제 - 실패 테스트 (예약 상태의 게시글)")
  @Test
  void 예약_상태의_게시글은_삭제할_수_없다() {
    // given
    Post post = customStatusPost(defaultCategory(), defaultBook(), MEMBER_ID, EMD_AREA_ID, PostStatus.IN_PROGRESS);
    RemovePostCommand command = new RemovePostCommand(MEMBER_ID, post.getId());
    given(postReader.readPostById(command.postId())).willReturn(post);

    // when & then
    assertThatThrownBy(() -> postService.removePostProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.UNREMOVABLE_POST_STATE.getMessage());

    then(postReader).should(times(1)).readPostById(post.getId());
    then(postFavoriteService).shouldHaveNoInteractions();
    assertThat(post.getPostStatus()).isEqualTo(PostStatus.IN_PROGRESS);
  }

  @DisplayName("게시글 삭제 - 실패 테스트 (삭제 상태의 게시글)")
  @Test
  void 삭제_상태의_게시글을_삭제하는_경우_예외를_발생시킨다() {
    // given
    Post post = customStatusPost(defaultCategory(), defaultBook(), MEMBER_ID, EMD_AREA_ID, PostStatus.REMOVED);
    RemovePostCommand command = new RemovePostCommand(MEMBER_ID, post.getId());
    given(postReader.readPostById(command.postId())).willReturn(post);

    // when & then
    assertThatThrownBy(() -> postService.removePostProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.ALREADY_REMOVED_POST_STATE.getMessage());

    then(postReader).should(times(1)).readPostById(post.getId());
    then(postFavoriteService).shouldHaveNoInteractions();
    assertThat(post.getPostStatus()).isEqualTo(PostStatus.REMOVED);
  }
}

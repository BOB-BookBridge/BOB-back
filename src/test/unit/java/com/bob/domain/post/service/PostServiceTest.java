package com.bob.domain.post.service;

import static com.bob.domain.post.entity.status.Status.ACTIVE;
import static com.bob.domain.post.entity.status.Status.REMOVED;
import static com.bob.domain.post.entity.status.Status.WITHHELD;
import static com.bob.domain.post.entity.status.TradeProgress.IN_PROGRESS;
import static com.bob.domain.post.entity.status.TradeProgress.READY;
import static com.bob.global.exception.response.ApplicationError.ALREADY_POST_FAVORITE;
import static com.bob.global.exception.response.ApplicationError.INVALID_POST_FAVORITE;
import static com.bob.global.exception.response.ApplicationError.NOT_VERIFIED_MEMBER;
import static com.bob.support.fixture.command.ChangePostCommandFixture.DEFAULT_CHANGE_POST_COMMAND;
import static com.bob.support.fixture.command.CreatePostCommandFixture.createPostCommandWithImageRefId;
import static com.bob.support.fixture.command.CreatePostCommandFixture.defaultCreatePostCommand;
import static com.bob.support.fixture.command.RegisterPostFavoriteCommandFixture.defaultRegisterPostFavoriteCommand;
import static com.bob.support.fixture.domain.BookFixture.DEFAULT_BOOK;
import static com.bob.support.fixture.domain.CategoryFixture.defaultCategory;
import static com.bob.support.fixture.domain.EmdAreaFixture.EMD_AREA_ID;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.PostFixture.DEFAULT_MOCK_POSTS;
import static com.bob.support.fixture.domain.PostFixture.customStatusPost;
import static com.bob.support.fixture.domain.PostFixture.defaultIdPost;
import static com.bob.support.fixture.domain.PostFixture.defaultPost;
import static com.bob.support.fixture.query.PostQueryFixture.defaultReadFilteredPostsQuery;
import static com.bob.support.fixture.query.PostQueryFixture.defaultReadMemberFavoritePostsQuery;
import static com.bob.support.fixture.query.PostQueryFixture.searchAuthorQuery;
import static com.bob.support.fixture.query.PostQueryFixture.searchCategoryQuery;
import static com.bob.support.fixture.response.AreaSummaryResponseFixture.DEFAULT_AREA_SUMMARY;
import static com.bob.support.fixture.response.AreaSummaryResponseFixture.NOT_VALID_AREA_SUMMARY;
import static com.bob.support.fixture.response.BookResponseFixture.DEFAULT_BOOK_RESPONSE;
import static com.bob.support.fixture.response.MemberProfileResponseFixture.DEFAULT_MEMBER_PROFILE_RESPONSE;
import static com.bob.support.fixture.response.PostFileSummaryResponseFixture.DEFAULT_READ_FILES_RESPONSE;
import static com.bob.support.fixture.response.PostResponseFixture.DEFAULT_FAVORITE_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;

import com.bob.domain.member.service.dto.command.RegisterMemberBookCommand;
import com.bob.domain.post.entity.Category;
import com.bob.domain.post.entity.Post;
import com.bob.domain.post.entity.status.Status;
import com.bob.domain.post.entity.status.TradeProgress;
import com.bob.domain.post.repository.PostRepository;
import com.bob.domain.post.service.dto.command.ChangeMemberPostStatusCommand;
import com.bob.domain.post.service.dto.command.ChangePostCommand;
import com.bob.domain.post.service.dto.command.ChangeTradeProgressCommand;
import com.bob.domain.post.service.dto.command.CreatePostCommand;
import com.bob.domain.post.service.dto.command.RegisterPostFavoriteCommand;
import com.bob.domain.post.service.dto.command.RemovePostCommand;
import com.bob.domain.post.service.dto.query.ReadFilteredPostsQuery;
import com.bob.domain.post.service.dto.query.ReadPostDetailQuery;
import com.bob.domain.post.service.dto.query.ReadPostFavoritesQuery;
import com.bob.domain.post.service.dto.query.condition.SearchKey;
import com.bob.domain.post.service.dto.response.PostCreateResponse;
import com.bob.domain.post.service.dto.response.PostDetailResponse;
import com.bob.domain.post.service.dto.response.PostsResult;
import com.bob.domain.post.service.port.out.PostAreaPort;
import com.bob.domain.post.service.port.out.PostBookPort;
import com.bob.domain.post.service.port.out.PostFilePort;
import com.bob.domain.post.service.port.out.PostMemberPort;
import com.bob.domain.post.service.reader.CategoryReader;
import com.bob.domain.post.service.reader.PostReader;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
  private CategoryReader categoryReader;

  @Mock
  private PostBookPort bookPort;

  @Mock
  private PostMemberPort memberPort;

  @Mock
  private PostAreaPort areaPort;

  @Mock
  private PostFilePort filePort;

  private Pageable pageable = PageRequest.of(0, 12);

  @Test
  void 게시글_정상_등록() {
    // given
    CreatePostCommand command = defaultCreatePostCommand();
    Category category = defaultCategory();
    Long bookId = DEFAULT_BOOK.getId();
    given(areaPort.readPostAreaSummary(MEMBER_ID)).willReturn(DEFAULT_AREA_SUMMARY);
    given(categoryReader.readCategoryById(command.categoryId())).willReturn(category);
    given(bookPort.createBook(command.toCreateBookCommand())).willReturn(bookId);
    given(memberPort.createMemberBook(any(RegisterMemberBookCommand.class))).willReturn(1L);
    given(postRepository.save(any(Post.class))).willAnswer(invocation -> invocation.getArgument(0));

    ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);

    // when
    PostCreateResponse response = postService.createPostProcess(command);

    // then
    then(bookPort).should().createBook(command.toCreateBookCommand());
    then(categoryReader).should().readCategoryById(command.categoryId());
    then(postRepository).should(times(1)).save(captor.capture());
    then(filePort).should(times(1)).modifyReferenceId(command.fileNames(), String.valueOf(response.postId()));
    then(memberPort).should(times(1)).createMemberBook(command.toCreateMemberBookCommand(bookId));

    Post post = captor.getValue();
    assertThat(post.getBookId()).isEqualTo(bookId);
    assertThat(post.getSellerId()).isEqualTo(MEMBER_ID);
    assertThat(post.getCategory()).isEqualTo(category);
    assertThat(post.isWishOnly()).isFalse();
  }

  @Test
  void 게시글_등록_시_referenceId가_null이면_이미지_매핑을_하지_않는다() {
    // given
    CreatePostCommand command = createPostCommandWithImageRefId(null);
    Long bookId = DEFAULT_BOOK.getId();
    given(areaPort.readPostAreaSummary(MEMBER_ID)).willReturn(DEFAULT_AREA_SUMMARY);
    given(categoryReader.readCategoryById(command.categoryId())).willReturn(defaultCategory());
    given(bookPort.createBook(command.toCreateBookCommand())).willReturn(bookId);
    given(memberPort.createMemberBook(any(RegisterMemberBookCommand.class))).willReturn(1L);
    given(postRepository.save(any(Post.class))).willAnswer(invocation -> invocation.getArgument(0));

    // when
    postService.createPostProcess(command);

    // then
    then(filePort).shouldHaveNoInteractions();
  }

  @Test
  void 게시글_등록_시_referenceId가_공백이면_이미지_매핑을_하지_않는다() {
    // given
    CreatePostCommand command = createPostCommandWithImageRefId(List.of());
    Long bookId = DEFAULT_BOOK.getId();
    given(areaPort.readPostAreaSummary(MEMBER_ID)).willReturn(DEFAULT_AREA_SUMMARY);
    given(categoryReader.readCategoryById(command.categoryId())).willReturn(defaultCategory());
    given(bookPort.createBook(command.toCreateBookCommand())).willReturn(bookId);
    given(memberPort.createMemberBook(any(RegisterMemberBookCommand.class))).willReturn(1L);
    given(postRepository.save(any(Post.class))).willAnswer(invocation -> invocation.getArgument(0));

    // when
    postService.createPostProcess(command);

    // then
    then(filePort).shouldHaveNoInteractions();
  }

  @Test
  void 게시글_등록_시_위치인증_되지_않은_사용자는_등록할_수_없다() {
    // given
    CreatePostCommand command = defaultCreatePostCommand();
    given(areaPort.readPostAreaSummary(MEMBER_ID)).willReturn(NOT_VALID_AREA_SUMMARY);

    // when & then
    assertThatThrownBy(() -> postService.createPostProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(NOT_VERIFIED_MEMBER.getMessage());
  }

  @Test
  void 게시글_좋아요_정상_등록() {
    // given
    RegisterPostFavoriteCommand command = defaultRegisterPostFavoriteCommand();
    Post post = defaultIdPost(defaultCategory(), DEFAULT_BOOK, MEMBER_ID, EMD_AREA_ID);
    Long postId = command.postId();

    given(postReader.readPostById(postId)).willReturn(post);

    // when
    postService.registerPostFavoriteProcess(command);

    // then
    then(postReader).should().readPostById(postId);
    then(postFavoriteService).should().createPostFavoriteProcess(MEMBER_ID, post);
    then(postRepository).should().increaseFavoriteCount(postId);
  }

  @Test
  void 게시글_좋아요_중복_시_예외가_발생한다() {
    // given
    RegisterPostFavoriteCommand command = defaultRegisterPostFavoriteCommand();
    Post post = defaultIdPost(defaultCategory(), DEFAULT_BOOK, MEMBER_ID, EMD_AREA_ID);
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

  @Test
  void 게시글_좋아요_정상_해제() {
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

  @Test
  void 게시글_좋아요_해제_시_좋아요하지_않은_게시글은_해제할_수_없다() {
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

  @Test
  void 게시글_목록_정상_조회() {
    // given
    ReadFilteredPostsQuery query = defaultReadFilteredPostsQuery();
    given(postReader.readFilteredPosts(query, pageable)).willReturn(DEFAULT_MOCK_POSTS());
    given(postRepository.countFilteredPosts(query)).willReturn(2L);

    // when
    PostsResult result = postService.readFilteredPostsProcess(query, pageable);

    // then
    assertThat(result.totalCount()).isEqualTo(2L);
    then(postReader).should(times(1)).readFilteredPosts(query, pageable);
    then(postRepository).should(times(1)).countFilteredPosts(query);
  }

  @Test
  void 게시글_목록_조회_카테고리_조회_시_자식_카테고리_포함_조회() {
    // given
    ReadFilteredPostsQuery query = searchCategoryQuery(); // categoryId = 1, 1의 자식 = 12, 13, 14, 15, 16
    given(categoryReader.readChildCategoryIds(query.categoryIds().get(0))).willReturn(List.of(12, 13, 14, 15, 16));
    given(postReader.readFilteredPosts(query, pageable)).willReturn(DEFAULT_MOCK_POSTS());
    given(postRepository.countFilteredPosts(query)).willReturn(2L);

    // when
    PostsResult result = postService.readFilteredPostsProcess(query, pageable);

    // then
    then(categoryReader).should(times(1)).readChildCategoryIds(query.categoryIds().get(0));
    assertThat(query.categoryIds()).contains(12, 13, 14, 15, 16); // 서비스 단에서 게시글 목록 조회 전 자식 카테고리를 조회 후 카테고리 조건에 추가

    then(postReader).should(times(1)).readFilteredPosts(query, pageable);
    then(postRepository).should(times(1)).countFilteredPosts(query);
    assertThat(result.totalCount()).isEqualTo(2L);
  }

  @Test
  void 게시글_목록_조회_키워드_조회_시_관련_책_ID_조회() {
    // given
    ReadFilteredPostsQuery query = ReadFilteredPostsQuery.builder()
        .key(SearchKey.AUTHOR)
        .keyword("조영호")
        .bookIds(new ArrayList<>())
        .build();
    given(bookPort.searchBookIds("AUTHOR", "조영호")).willReturn(List.of(1L, 2L));
    given(postReader.readFilteredPosts(query, pageable)).willReturn(DEFAULT_MOCK_POSTS());
    given(postRepository.countFilteredPosts(query)).willReturn(2L);

    // when
    PostsResult result = postService.readFilteredPostsProcess(query, pageable);

    // then
    assertThat(query.bookIds()).containsExactly(1L, 2L);
    assertThat(result.totalCount()).isEqualTo(2L);
    then(bookPort).should(times(1)).searchBookIds(query.key().name(), query.keyword());
    then(postReader).should(times(1)).readFilteredPosts(query, pageable);
    then(postRepository).should(times(1)).countFilteredPosts(query);
  }
  @Test
  void 게시글_목록_조회_키워드가_포함된_게시글이_없는_경우_빈_리스트_반환() {
    // given
    ReadFilteredPostsQuery query = searchAuthorQuery();
    given(bookPort.searchBookIds(query.key().name(), query.keyword())).willReturn(List.of());
    given(postReader.readFilteredPosts(query, pageable)).willReturn(List.of());
    given(postRepository.countFilteredPosts(query)).willReturn(0L);

    // when
    PostsResult result = postService.readFilteredPostsProcess(query, pageable);

    // then
    assertThat(result.totalCount()).isEqualTo(0L);
    then(bookPort).should(times(1)).searchBookIds(query.key().name(), query.keyword());

    assertThat(query.bookIds()).isEmpty();
  }

  @Test
  void 게시글_목록_조회_키워드가_null인_경우_동작하지_않는다() {
    // given
    ReadFilteredPostsQuery query = defaultReadFilteredPostsQuery();
    given(postReader.readFilteredPosts(query, pageable)).willReturn(DEFAULT_MOCK_POSTS());
    given(postRepository.countFilteredPosts(query)).willReturn(2L);

    // when
    postService.readFilteredPostsProcess(query, pageable);

    // then
    then(bookPort).shouldHaveNoInteractions();
  }

  @Test
  void 좋아요_게시글_목록_정상_조회() {
    // given
    UUID memberId = UUID.randomUUID();
    ReadPostFavoritesQuery query = defaultReadMemberFavoritePostsQuery(memberId);
    given(postFavoriteService.readMemberFavoritePosts(memberId, pageable)).willReturn(DEFAULT_FAVORITE_RESPONSE());

    // when
    PostsResult result = postService.readPostFavoritesProcess(query, pageable);

    // then
    assertThat(result.totalCount()).isEqualTo(2L);
    then(postFavoriteService).should(times(1)).readMemberFavoritePosts(query.memberId(), pageable);
  }

  @Test
  void 게시글_상세_조회_작성자와_조회자가_같다면_isOwner는_true이다() {
    // given
    Post post = defaultPost(defaultCategory(), DEFAULT_BOOK, MEMBER_ID, EMD_AREA_ID);
    ReadPostDetailQuery query = new ReadPostDetailQuery(MEMBER_ID, post.getId(), true);
    given(postReader.readPostById(post.getId())).willReturn(post);
    given(bookPort.readBookSummary(DEFAULT_BOOK.getId())).willReturn(DEFAULT_BOOK_RESPONSE);
    given(memberPort.readPostMemberSummary(MEMBER_ID)).willReturn(DEFAULT_MEMBER_PROFILE_RESPONSE);
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

  @Test
  void 게시글_상세_조회_작성자와_조회자가_다르면_isOwner는_false이다() {
    // given
    Post post = defaultPost(defaultCategory(), DEFAULT_BOOK, MEMBER_ID, EMD_AREA_ID);
    UUID otherMemberId = UUID.randomUUID();

    ReadPostDetailQuery query = new ReadPostDetailQuery(otherMemberId, post.getId(), true);
    given(postReader.readPostById(post.getId())).willReturn(post);
    given(bookPort.readBookSummary(post.getBookId())).willReturn(DEFAULT_BOOK_RESPONSE);
    given(memberPort.readPostMemberSummary(MEMBER_ID)).willReturn(DEFAULT_MEMBER_PROFILE_RESPONSE);
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

  @ParameterizedTest(name = "클라이언트는 [{index}] {0}의 게시글 조회 불가")
  @MethodSource("inaccessiblePostCases")
  void 게시글_상세_조회_보류_삭제_상태(String caseName, boolean isRemoved) {
    // given
    Post post = isRemoved
        ? customStatusPost(defaultCategory(), DEFAULT_BOOK, MEMBER_ID, EMD_AREA_ID, READY, REMOVED)
        : defaultPost(defaultCategory(), DEFAULT_BOOK, MEMBER_ID, EMD_AREA_ID);

    if (!isRemoved) {
      post.updateStatus(WITHHELD);
    }

    ReadPostDetailQuery query = new ReadPostDetailQuery(UUID.randomUUID(), post.getId(), true);

    given(postReader.readPostById(post.getId())).willReturn(post);
    willDoNothing().given(postRepository).increaseViewCount(post.getId());

    // when & then
    assertThatThrownBy(() -> postService.readPostDetailProcess(query))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.NOT_ACCESSIBLE_POST.getMessage());

    then(postRepository).should(times(1)).increaseViewCount(post.getId());
    then(postReader).should(times(1)).readPostById(post.getId());
    then(memberPort).shouldHaveNoInteractions();
    then(filePort).shouldHaveNoInteractions();
    then(postFavoriteService).shouldHaveNoInteractions();
  }

  private static Stream<Arguments> inaccessiblePostCases() {
    return Stream.of(
        Arguments.of("삭제 상태", true),  // isRemoved = true
        Arguments.of("보류 상태", false)  // isRemoved = false (withhold = true)
    );
  }

  @Test
  void 게시글_정상_수정() {
    // given
    Post post = defaultPost(defaultCategory(), DEFAULT_BOOK, MEMBER_ID, EMD_AREA_ID);
    ChangePostCommand command = DEFAULT_CHANGE_POST_COMMAND(MEMBER_ID, post.getId());
    given(postReader.readPostById(post.getId())).willReturn(post);

    // when
    postService.changePostProcess(command);

    // then
    assertThat(post.getSellPrice()).isEqualTo(12000);
    assertThat(post.getDescription()).isEqualTo("상태 좋음");
    then(postReader).should().readPostById(post.getId());
  }

  @Test
  void 게시글_수정_시_타인의_게시글은_수정할_수_없다() {
    // given
    Post post = defaultPost(defaultCategory(), DEFAULT_BOOK, MEMBER_ID, EMD_AREA_ID);
    UUID otherMemberId = UUID.randomUUID();
    given(postReader.readPostById(post.getId())).willReturn(post);
    ChangePostCommand command = DEFAULT_CHANGE_POST_COMMAND(otherMemberId, post.getId());

    // when & then
    assertThatThrownBy(() -> postService.changePostProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.NOT_POST_OWNER.getMessage());

    then(postReader).should().readPostById(post.getId());
  }

  @Test
  void 게시글_상태_정상_수정() {
    // given
    Post post = defaultPost(defaultCategory(), DEFAULT_BOOK, MEMBER_ID, EMD_AREA_ID);
    ChangeTradeProgressCommand command = new ChangeTradeProgressCommand(post.getId(), "IN_PROGRESS");
    given(postReader.readPostById(post.getId())).willReturn(post);

    // when
    postService.changeTradeProgressProcess(command);

    // then
    assertThat(post.getTradeProgress()).isEqualTo(IN_PROGRESS);
    then(postReader).should().readPostById(post.getId());
  }

  @Test
  void 게시글_정상_삭제() {
    // given
    Post post = defaultPost(defaultCategory(), DEFAULT_BOOK, MEMBER_ID, EMD_AREA_ID);
    RemovePostCommand command = new RemovePostCommand(MEMBER_ID, post.getId());
    given(postReader.readPostById(command.postId())).willReturn(post);

    // when
    postService.removePostProcess(command);

    // then
    then(postReader).should(times(1)).readPostById(post.getId());
    then(postFavoriteService).should(times(1)).removePostFavoriteProcess(post.getId());
    then(memberPort).should(times(1)).removeMemberBookUsage(post.getId());
    assertThat(post.getStatus()).isEqualTo(Status.REMOVED);
  }

  @Test
  void 게시글_삭제_시_타인이_게시글을_삭제할_수_없다() {
    // given
    Post post = defaultPost(defaultCategory(), DEFAULT_BOOK, MEMBER_ID, EMD_AREA_ID);
    UUID otherMemberId = UUID.randomUUID();

    RemovePostCommand command = new RemovePostCommand(otherMemberId, post.getId());

    given(postReader.readPostById(command.postId())).willReturn(post);

    // when & then
    assertThatThrownBy(() -> postService.removePostProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.NOT_POST_OWNER.getMessage());

    then(postReader).should(times(1)).readPostById(post.getId());
    then(postFavoriteService).shouldHaveNoInteractions();
    assertThat(post.getTradeProgress()).isEqualTo(TradeProgress.READY);
  }

  @Test
  void 게시글_삭제_예약_상태인_게시글은_삭제할_수_없다() {
    // given
    Post post = customStatusPost(defaultCategory(), DEFAULT_BOOK, MEMBER_ID, EMD_AREA_ID, IN_PROGRESS, ACTIVE);
    RemovePostCommand command = new RemovePostCommand(MEMBER_ID, post.getId());
    given(postReader.readPostById(command.postId())).willReturn(post);

    // when & then
    assertThatThrownBy(() -> postService.removePostProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.UNREMOVABLE_POST_STATE.getMessage());

    then(postReader).should(times(1)).readPostById(post.getId());
    then(postFavoriteService).shouldHaveNoInteractions();
    assertThat(post.getTradeProgress()).isEqualTo(IN_PROGRESS);
    assertThat(post.getStatus()).isEqualTo(ACTIVE);
  }

  @Test
  void 게시글_삭제_중복_요청_시_예외를_반환한다() {
    // given
    Post post = customStatusPost(defaultCategory(), DEFAULT_BOOK, MEMBER_ID, EMD_AREA_ID, READY, REMOVED);
    RemovePostCommand command = new RemovePostCommand(MEMBER_ID, post.getId());
    given(postReader.readPostById(command.postId())).willReturn(post);

    // when & then
    assertThatThrownBy(() -> postService.removePostProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.ALREADY_REMOVED_POST_STATE.getMessage());

    then(postReader).should(times(1)).readPostById(post.getId());
    then(postFavoriteService).shouldHaveNoInteractions();
    assertThat(post.getStatus()).isEqualTo(REMOVED);
  }

  @ParameterizedTest(name = "{index} → {0}")
  @MethodSource("accountEventParam")
  void 계정_이벤트를_통한_게시글_처리(Status inputStatus, Status expectedStatus, int expectedScrapRemovalsPerPost) {
    // given
    List<Post> posts = DEFAULT_MOCK_POSTS();
    given(postReader.readPostsByMember(MEMBER_ID)).willReturn(posts);

    ChangeMemberPostStatusCommand command = new ChangeMemberPostStatusCommand(MEMBER_ID, inputStatus);

    // when
    postService.changeStatusByAccountEventProcess(command);

    // then
    assertThat(posts).hasSize(2);
    assertThat(posts.get(0).getStatus()).isEqualTo(expectedStatus);
    assertThat(posts.get(1).getStatus()).isEqualTo(expectedStatus);

    int totalExpectedCalls = expectedScrapRemovalsPerPost * posts.size();
    then(postFavoriteService).should(times(totalExpectedCalls)).removePostFavoriteProcess(anyLong());

    if (expectedScrapRemovalsPerPost > 0) {
      then(postFavoriteService).should().removePostFavoriteProcess(1L);
      then(postFavoriteService).should().removePostFavoriteProcess(2L);
    }
    then(postReader).should(times(1)).readPostsByMember(MEMBER_ID);
  }

  static Stream<Arguments> accountEventParam() {
    return Stream.of(
        Arguments.of(ACTIVE, ACTIVE, 0),
        Arguments.of(REMOVED, REMOVED, 1)
    );
  }
}

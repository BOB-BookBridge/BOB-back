package com.bob.domain.post.service;

import static com.bob.domain.post.entity.status.Status.REMOVED;
import static com.bob.global.exception.response.ApplicationError.ALREADY_POST_FAVORITE;
import static com.bob.global.exception.response.ApplicationError.INVALID_POST_FAVORITE;
import static com.bob.global.exception.response.ApplicationError.NOT_POST_OWNER;
import static com.bob.global.exception.response.ApplicationError.NOT_VERIFIED_MEMBER;
import static com.bob.support.fixture.command.ChangePostCommandFixture.DEFAULT_CHANGE_POST_COMMAND;
import static com.bob.support.fixture.command.CreatePostCommandFixture.FILE_NAMES;
import static com.bob.support.fixture.command.CreatePostCommandFixture.defaultCreatePostCommand;
import static com.bob.support.fixture.domain.CategoryFixture.defaultCategory;
import static com.bob.support.fixture.query.PostQueryFixture.defaultReadFilteredPostsQuery;
import static com.bob.support.fixture.query.PostQueryFixture.searchAuthorQuery;
import static com.bob.support.fixture.query.PostQueryFixture.searchBetween_5000_10000_PriceQuery;
import static com.bob.support.fixture.query.PostQueryFixture.searchBookStatusQuery;
import static com.bob.support.fixture.query.PostQueryFixture.searchCategoryQuery;
import static com.bob.support.fixture.query.PostQueryFixture.searchHighPriceQuery;
import static com.bob.support.fixture.query.PostQueryFixture.searchLowPriceQuery;
import static com.bob.support.fixture.query.PostQueryFixture.searchNewestQuery;
import static com.bob.support.fixture.query.PostQueryFixture.searchOldestQuery;
import static com.bob.support.fixture.query.PostQueryFixture.searchTitleQuery;
import static com.bob.support.fixture.query.PostQueryFixture.searchTradeStatusQuery;
import static com.bob.support.fixture.query.PostQueryFixture.searchUnder5000PriceQuery;
import static com.bob.support.fixture.response.AreaSummaryResponseFixture.DEFAULT_AREA_SUMMARY;
import static com.bob.support.fixture.response.AreaSummaryResponseFixture.NOT_VALID_AREA_SUMMARY;
import static com.bob.support.fixture.response.PostFileSummaryResponseFixture.DEFAULT_READ_FILES_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.domain.book.entity.Book;
import com.bob.domain.book.repository.BookRepository;
import com.bob.domain.post.entity.Category;
import com.bob.domain.post.repository.CategoryRepository;
import com.bob.domain.post.entity.Post;
import com.bob.domain.post.entity.PostFavorite;
import com.bob.domain.post.entity.status.TradeProgress;
import com.bob.domain.post.repository.PostFavoriteRepository;
import com.bob.domain.post.repository.PostRepository;
import com.bob.domain.post.service.dto.command.ChangePostCommand;
import com.bob.domain.post.service.dto.command.ChangeTradeProgressCommand;
import com.bob.domain.post.service.dto.command.CreatePostCommand;
import com.bob.domain.post.service.dto.command.RegisterPostFavoriteCommand;
import com.bob.domain.post.service.dto.command.RemovePostCommand;
import com.bob.domain.post.service.dto.query.ReadFilteredPostsQuery;
import com.bob.domain.post.service.dto.query.ReadPostDetailQuery;
import com.bob.domain.post.service.dto.query.ReadPostFavoritesQuery;
import com.bob.domain.post.service.dto.response.PostDetailResponse;
import com.bob.domain.post.service.dto.response.PostSummary;
import com.bob.domain.post.service.dto.response.PostsResponse;
import com.bob.domain.post.service.port.out.PostAreaPort;
import com.bob.domain.post.service.port.out.PostFilePort;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.TestContainerSupport;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("게시글 서비스 통합 테스트")
@Transactional
@SpringBootTest
class PostServiceIntgTest extends TestContainerSupport {

  @Autowired
  private PostService postService;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private PostFavoriteService postFavoriteService;

  @Autowired
  private PostFavoriteRepository postFavoriteRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @MockitoBean
  private PostAreaPort areaPort;

  @MockitoBean
  private PostFilePort filePort;

  @PersistenceContext
  private EntityManager em;

  private PageRequest pageable = PageRequest.of(0, 12);

  @Test
  @DisplayName("게시글 등록 - 성공 테스트")
  void 위치_인증이_된_사용자는_게시글을_등록할_수_있다() {
    // given
    UUID memberId = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
    Category category = categoryRepository.save(defaultCategory());
    CreatePostCommand command = defaultCreatePostCommand(memberId, category.getId(), FILE_NAMES);
    given(areaPort.readPostAreaSummary(memberId)).willReturn(DEFAULT_AREA_SUMMARY);
    int beforePostCount = postRepository.findAllBySellerId(memberId).size();
    int expectedPostCount = beforePostCount + 1;

    // when
    postService.createPostProcess(command);

    // then
    List<Post> posts = postRepository.findAllBySellerId(memberId);
    assertThat(posts).hasSize(expectedPostCount);

    Post currentPost = posts.get(expectedPostCount - 1);
    assertThat(currentPost.getSellerId()).isEqualTo(memberId);
    assertThat(currentPost.getCategory().getId()).isEqualTo(category.getId());

    Book book = bookRepository.findByIsbn13(command.bookIsbn()).get();
    assertThat(book).isNotNull();
    assertThat(book.getTitle()).isEqualTo(command.bookTitle());
  }

  @Test
  @DisplayName("게시글 등록 - 이미지 매핑 생략 (null)")
  void 게시글_등록시_referenceId가_null이면_이미지_매핑을_하지_않는다() {
    // given
    UUID memberId = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
    Category category = categoryRepository.save(defaultCategory());
    CreatePostCommand command = defaultCreatePostCommand(memberId, category.getId(), null);
    given(areaPort.readPostAreaSummary(memberId)).willReturn(DEFAULT_AREA_SUMMARY);

    // when
    postService.createPostProcess(command);

    // then
    then(filePort).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("게시글 등록 - 실패 테스트(위치 인증 X)")
  void 위치_인증이_안된_사용자는_게시글을_등록할_수_없다() {
    // given
    UUID memberId = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
    given(areaPort.readPostAreaSummary(memberId)).willReturn(NOT_VALID_AREA_SUMMARY);
    Category category = categoryRepository.save(defaultCategory());
    CreatePostCommand command = defaultCreatePostCommand(memberId, category.getId(), FILE_NAMES);

    // when & then
    assertThatThrownBy(() -> postService.createPostProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(NOT_VERIFIED_MEMBER.getMessage());
  }

  @DisplayName("사용자 즐겨찾기 게시글 조회 테스트 - 성공 테스트")
  @Test
  void 회원이_좋아요한_게시글을_조회할_수_있다() {
    // given
    UUID memberId = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
    ReadPostFavoritesQuery query = ReadPostFavoritesQuery.of(memberId);

    // when
    PostsResponse response = postService.readPostFavoritesProcess(query, pageable);

    // then
    assertThat(response.totalCount()).isEqualTo(2);
    assertThat(response.posts())
        .extracting(PostSummary::postTitle)
        .containsExactlyInAnyOrder("자바의 정석", "토비의 스프링");
  }

  @DisplayName("사용자 즐겨찾기 게시글 조회 테스트 - 성공 테스트(결과없음)")
  @Test
  void 회원이_좋아요한_게시글이_없는_경우_빈_리스트를_반환한다() {
    // given
    UUID memberId = UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59");
    ReadPostFavoritesQuery query = ReadPostFavoritesQuery.of(memberId);

    // when
    PostsResponse response = postService.readPostFavoritesProcess(query, pageable);

    // then
    assertThat(response.totalCount()).isEqualTo(0);
    assertThat(response.posts()).hasSize(0);
  }

  @Test
  @DisplayName("게시글 좋아요 등록 - 성공 테스트")
  void 사용자가_게시글을_좋아요_누를_수_있다() {
    // given
    UUID memberId = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
    Post post = postRepository.findById(10L).get();

    RegisterPostFavoriteCommand command = new RegisterPostFavoriteCommand(memberId, post.getId());

    // when
    postService.registerPostFavoriteProcess(command);
    clearPersistContext();

    // then
    Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
    PostFavorite postFavorite = postFavoriteRepository.findByMemberIdAndPostId(memberId, post.getId()).get();
    assertThat(updatedPost.getScrapCount()).isEqualTo(post.getScrapCount() + 1);
    assertThat(postFavorite).isNotNull();
  }

  @Test
  @DisplayName("게시글 좋아요 등록 - 실패 테스트 (이미 좋아요한 경우)")
  void 사용자가_이미_좋아요한_게시글에_중복_좋아요시_예외가_발생한다() {
    // given
    UUID memberId = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
    Post post = postRepository.findById(10L).get();
    RegisterPostFavoriteCommand command = new RegisterPostFavoriteCommand(memberId, post.getId());
    postService.registerPostFavoriteProcess(command);

    // when & then
    assertThatThrownBy(() -> postService.registerPostFavoriteProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining(ALREADY_POST_FAVORITE.getMessage());
  }

  @Test
  @DisplayName("게시글 좋아요 해제 - 성공 테스트")
  void 사용자가_게시글의_좋아요를_해제할_수_있다() {
    // given
    UUID memberId = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
    Post post = postRepository.findById(10L).get();
    RegisterPostFavoriteCommand command = new RegisterPostFavoriteCommand(memberId, post.getId());
    postService.registerPostFavoriteProcess(command);
    clearPersistContext();
    int before = postRepository.findById(post.getId()).get().getScrapCount();

    // when
    postService.unregisterPostFavoriteProcess(command);
    clearPersistContext();

    // then
    Optional<PostFavorite> result = postFavoriteRepository.findByMemberIdAndPostId(memberId, post.getId());
    int after = postRepository.findById(post.getId()).get().getScrapCount();

    assertThat(result).isEmpty();
    assertThat(after).isEqualTo(before - 1);
  }

  @Test
  @DisplayName("게시글 좋아요 해제 - 실패 테스트 (좋아요하지 않은 경우)")
  void 좋아요를_누르지_않은_게시글에_해제요청하면_예외발생한다() {
    // given
    UUID memberId = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
    Post post = postRepository.findById(10L).get();
    RegisterPostFavoriteCommand command = new RegisterPostFavoriteCommand(memberId, post.getId());

    // when & then
    assertThatThrownBy(() -> postService.unregisterPostFavoriteProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining(INVALID_POST_FAVORITE.getMessage());
  }

  @Test
  @DisplayName("제목 검색 - '오브젝트' 포함 게시글 조회")
  void 제목으로_게시글을_검색할_수_있다() {
    ReadFilteredPostsQuery query = searchTitleQuery();
    PostsResponse result = postService.readFilteredPostsProcess(query, pageable);

    assertThat(result.posts())
        .extracting(PostSummary::postTitle)
        .allMatch(title -> title.contains("오브젝트"));
  }

  @Test
  @DisplayName("저자 검색 - '김영한' 포함 게시글 조회")
  void 저자로_게시글을_검색할_수_있다() {
    ReadFilteredPostsQuery query = searchAuthorQuery();
    PostsResponse result = postService.readFilteredPostsProcess(query, pageable);

    assertThat(result.posts())
        .extracting(PostSummary::postTitle)
        .contains("자바 ORM 표준 JPA 프로그래밍");
  }

  @Test
  @DisplayName("가격 필터 - 5000원 이하 게시글 조회")
  void 가격이_5000원이하인_게시글만_조회할_수_있다() {
    ReadFilteredPostsQuery query = searchUnder5000PriceQuery();
    PostsResponse result = postService.readFilteredPostsProcess(query, pageable);

    assertThat(result.posts())
        .extracting(PostSummary::sellPrice)
        .allMatch(price -> price <= 5000);
  }

  @Test
  @DisplayName("가격 필터 - 5000원 이상, 10000원 이하 게시글 조회")
  void 가격이_5000원이상_10000원이하인_게시글만_조회할_수_있다() {
    ReadFilteredPostsQuery query = searchBetween_5000_10000_PriceQuery();
    PostsResponse result = postService.readFilteredPostsProcess(query, pageable);

    assertThat(result.posts())
        .extracting(PostSummary::sellPrice)
        .allMatch(price -> price >= 5000 && price <= 10000);
  }

  @Test
  @DisplayName("카테고리 필터 - categoryId = 1")
  void 카테고리별_게시글을_조회할_수_있다() {
    ReadFilteredPostsQuery query = searchCategoryQuery();
    PostsResponse result = postService.readFilteredPostsProcess(query, pageable);

    assertThat(query.categoryIds()).contains(1, 12, 13, 14, 15, 16); // 1의 자식 카테고리는 12, 13, 14, 15, 16
    assertThat(result.posts())
        .extracting(PostSummary::categoryId)
        .contains(1);
  }

  @Test
  @DisplayName("거래 상태 필터 - 거래 완료")
  void 거래상태로_게시글을_조회할_수_있다() {
    ReadFilteredPostsQuery query = searchTradeStatusQuery();
    PostsResponse result = postService.readFilteredPostsProcess(query, pageable);

    assertThat(result.posts())
        .extracting(PostSummary::postStatus)
        .containsOnly("COMPLETED");
  }

  @Test
  void 삭제_된_게시글은_조회_목록에서_제외된다() {
    // given
    long allPostsCount = postRepository.count();
    long removedPostsCount = jdbcTemplate.queryForObject(
        """
          SELECT COUNT(*) 
          FROM posts
          WHERE status = 'REMOVED'
        """, Long.class
    );
    ReadFilteredPostsQuery query = defaultReadFilteredPostsQuery(); // 기본 게시글 조회 쿼리 (app 기본)
    pageable = PageRequest.of(0, Integer.MAX_VALUE); // 모든 게시글 개수 조회를 위한 page limit 수정

    // when
    PostsResponse result = postService.readFilteredPostsProcess(query, pageable);

    // then
    assertThat(removedPostsCount).isNotZero();
    assertThat(result.posts()).hasSize((int) (allPostsCount - removedPostsCount));
  }

  @Test
  @DisplayName("책 상태 필터 - 최상")
  void 책상태로_게시글을_조회할_수_있다() {
    ReadFilteredPostsQuery query = searchBookStatusQuery();
    PostsResponse result = postService.readFilteredPostsProcess(query, pageable);

    assertThat(result.posts())
        .extracting(PostSummary::bookStatus)
        .contains("BEST");
  }

  @Test
  @DisplayName("정렬 - 최신순 (createdAt DESC)")
  void 최신순으로_게시글을_정렬할_수_있다() {
    ReadFilteredPostsQuery query = searchNewestQuery();
    PostsResponse result = postService.readFilteredPostsProcess(query, pageable);

    List<LocalDateTime> createdAtList = result.posts().stream()
        .map(PostSummary::createdAt)
        .toList();

    assertThat(createdAtList).isSortedAccordingTo(Comparator.reverseOrder());
  }

  @Test
  @DisplayName("정렬 - 오래된 순 (createdAt ASC)")
  void 오래된순으로_게시글을_정렬할_수_있다() {
    ReadFilteredPostsQuery query = searchOldestQuery();
    PostsResponse result = postService.readFilteredPostsProcess(query, pageable);

    List<LocalDateTime> createdAtList = result.posts().stream()
        .map(PostSummary::createdAt)
        .toList();

    assertThat(createdAtList).isSorted();
  }

  @Test
  @DisplayName("정렬 - 낮은 가격순")
  void 가격이_낮은_순으로_게시글을_정렬할_수_있다() {
    ReadFilteredPostsQuery query = searchLowPriceQuery();
    PostsResponse result = postService.readFilteredPostsProcess(query, pageable);

    List<Integer> prices = result.posts().stream()
        .map(PostSummary::sellPrice)
        .toList();

    assertThat(prices).isSorted();
  }

  @Test
  @DisplayName("정렬 - 높은 가격순")
  void 가격이_높은_순으로_게시글을_정렬할_수_있다() {
    ReadFilteredPostsQuery query = searchHighPriceQuery();
    PostsResponse result = postService.readFilteredPostsProcess(query, pageable);

    List<Integer> prices = result.posts().stream()
        .map(PostSummary::sellPrice)
        .toList();

    assertThat(prices).isSortedAccordingTo(Comparator.reverseOrder());
  }

  @Test
  @DisplayName("게시글 상세 조회 - 작성자 본인, 조회수 증가")
  void 게시글_상세_조회_작성자일_경우_isOwner_true_조회수_증가() {
    // given
    UUID writerId = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
    Post post = postRepository.findAllBySellerId(writerId).get(0); // 더미 데이터의 첫 번째 게시글
    int beforeViewCount = post.getViewCount();
    given(areaPort.readPostAreaSummary(writerId)).willReturn(DEFAULT_AREA_SUMMARY);
    given(filePort.readPostFileSummaries(post.getId())).willReturn(DEFAULT_READ_FILES_RESPONSE);

    // when
    PostDetailResponse result = postService.readPostDetailProcess(new ReadPostDetailQuery(writerId, post.getId(), true));
    clearPersistContext();

    // then
    Post updated = postRepository.findById(post.getId()).orElseThrow();
    assertThat(result.isOwner()).isTrue();
    assertThat(updated.getViewCount()).isEqualTo(beforeViewCount + 1);
  }

  @Test
  @DisplayName("게시글 상세 조회 - 요청자가 작성자가 아닐 경우 isOwner=false")
  void 게시글_상세_조회_작성자가_아닌_경우_isOwner_false() {
    // given
    UUID viewerId = UUID.randomUUID();
    UUID writerId = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
    Post post = postRepository.findAllBySellerId(writerId).get(0);
    given(areaPort.readPostAreaSummary(writerId)).willReturn(DEFAULT_AREA_SUMMARY);
    given(filePort.readPostFileSummaries(post.getId())).willReturn(DEFAULT_READ_FILES_RESPONSE);

    // when
    PostDetailResponse result = postService.readPostDetailProcess(new ReadPostDetailQuery(viewerId, post.getId(), true));

    // then
    assertThat(result.isOwner()).isFalse();
  }

  @Test
  @DisplayName("게시글 수정 - 성공 테스트")
  void 게시글_정보를_성공적으로_수정할_수_있다() {
    // given
    UUID writerId = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
    Post post = postRepository.findAll().iterator().next();
    ChangePostCommand command = DEFAULT_CHANGE_POST_COMMAND(writerId, post.getId());

    // when
    postService.changePostProcess(command);

    // then
    Post updated = postRepository.findById(post.getId()).orElseThrow();
    assertThat(updated.getSellPrice()).isEqualTo(command.sellPrice());
    assertThat(updated.getBookStatus().name()).isEqualTo(command.bookStatus());
    assertThat(updated.getDescription()).isEqualTo(command.description());
  }

  @Test
  @DisplayName("게시글 수정 - 작성자 불일치 예외")
  void 게시글_작성자가_아니면_예외가_발생한다() {
    // given
    UUID randomMemberId = UUID.randomUUID();
    Post post = postRepository.findAll().iterator().next();
    ChangePostCommand command = DEFAULT_CHANGE_POST_COMMAND(randomMemberId, post.getId());

    // when & then
    assertThatThrownBy(() -> postService.changePostProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining(NOT_POST_OWNER.getMessage());
  }

  @Test
  void 게시글_거래_상태_수정() {
    // given
    Post post = postRepository.findAll().iterator().next();
    ChangeTradeProgressCommand command = new ChangeTradeProgressCommand(post.getId(), "IN_PROGRESS");

    // when
    postService.changeTradeProgressProcess(command);

    // then
    Post updated = postRepository.findById(post.getId()).orElseThrow();
    assertThat(updated.getTradeProgress()).isEqualTo(TradeProgress.IN_PROGRESS);
  }


  @Test
  @DisplayName("게시글 삭제 - 작성자 본인이 삭제하면 게시글은 삭제 상태가 되고 좋아요는 모두 삭제된다")
  void 작성자_본인이_게시글을_삭제하면_게시글과_좋아요가_모두_삭제된다() {
    // given
    UUID writerId = UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59");
    UUID otherId = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
    given(areaPort.readPostAreaSummary(writerId)).willReturn(DEFAULT_AREA_SUMMARY);

    postService.createPostProcess(defaultCreatePostCommand(writerId, defaultCategory().getId(), FILE_NAMES));
    Post post = postRepository.findAllBySellerId(writerId).get(0);
    postService.registerPostFavoriteProcess(new RegisterPostFavoriteCommand(writerId, post.getId()));
    postService.registerPostFavoriteProcess(new RegisterPostFavoriteCommand(otherId, post.getId()));

    // when
    postService.removePostProcess(new RemovePostCommand(writerId, post.getId()));

    // then
    Optional<Post> deletedPost = postRepository.findById(post.getId());
    Optional<PostFavorite> writerFavorite = postFavoriteRepository.findByMemberIdAndPostId(writerId, post.getId());
    Optional<PostFavorite> otherFavorite = postFavoriteRepository.findByMemberIdAndPostId(otherId, post.getId());

    assertThat(deletedPost.get().getStatus()).isEqualTo(REMOVED);
    assertThat(writerFavorite).isEmpty();
    assertThat(otherFavorite).isEmpty();
  }

  @Test
  @DisplayName("게시글 삭제 - 작성자가 아닌 사용자가 삭제 시도하면 예외가 발생한다")
  void 작성자가_아닌_사용자가_게시글을_삭제하려고_하면_예외가_발생한다() {
    // given
    UUID writerId = UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59");
    UUID otherId = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
    given(areaPort.readPostAreaSummary(writerId)).willReturn(DEFAULT_AREA_SUMMARY);

    postService.createPostProcess(defaultCreatePostCommand(writerId, defaultCategory().getId(), FILE_NAMES));
    Post post = postRepository.findAllBySellerId(writerId).get(0);
    postService.registerPostFavoriteProcess(new RegisterPostFavoriteCommand(writerId, post.getId()));
    postService.registerPostFavoriteProcess(new RegisterPostFavoriteCommand(otherId, post.getId()));

    // when & then
    assertThatThrownBy(() -> postService.removePostProcess(new RemovePostCommand(otherId, post.getId())))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining(NOT_POST_OWNER.getMessage());

    // 삭제되지 않음
    assertThat(postRepository.findById(post.getId())).isPresent();
    assertThat(postFavoriteRepository.findAll())
        .anyMatch(fav -> fav.getPost().getId().equals(post.getId()));
  }


  private void clearPersistContext() {
    em.flush();
    em.clear();
  }
}

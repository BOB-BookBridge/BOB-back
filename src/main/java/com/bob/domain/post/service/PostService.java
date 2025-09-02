package com.bob.domain.post.service;

import static com.bob.domain.post.entity.status.Status.REMOVED;
import static com.bob.domain.post.entity.status.Status.WITHHELD;
import static com.bob.domain.post.entity.status.TradeProgress.IN_PROGRESS;
import static com.bob.domain.post.entity.status.TradeProgress.valueOf;
import static com.bob.domain.post.service.dto.response.PostFileSummaryResponse.from;
import static com.bob.global.exception.response.ApplicationError.ALREADY_REMOVED_POST_STATE;
import static com.bob.global.exception.response.ApplicationError.NOT_POST_OWNER;
import static com.bob.global.exception.response.ApplicationError.NOT_VERIFIED_MEMBER;
import static com.bob.global.exception.response.ApplicationError.UNREMOVABLE_POST_STATE;

import com.bob.domain.book.entity.Book;
import com.bob.domain.book.service.BookService;
import com.bob.domain.category.entity.Category;
import com.bob.domain.category.service.reader.CategoryReader;
import com.bob.domain.post.entity.Post;
import com.bob.domain.post.repository.PostRepository;
import com.bob.domain.post.service.dto.command.ChangePostCommand;
import com.bob.domain.post.service.dto.command.ChangeTradeProgressCommand;
import com.bob.domain.post.service.dto.command.CreatePostCommand;
import com.bob.domain.post.service.dto.command.RegisterPostFavoriteCommand;
import com.bob.domain.post.service.dto.command.RemovePostCommand;
import com.bob.domain.post.service.dto.command.ChangeMemberPostStatusCommand;
import com.bob.domain.post.service.dto.query.ReadFilteredPostsQuery;
import com.bob.domain.post.service.dto.query.ReadPostDetailQuery;
import com.bob.domain.post.service.dto.query.ReadPostFavoritesQuery;
import com.bob.domain.post.service.dto.response.PostAreaSummaryResponse;
import com.bob.domain.post.service.dto.response.PostCreateResponse;
import com.bob.domain.post.service.dto.response.PostDetailResponse;
import com.bob.domain.post.service.dto.response.PostFavoritesResponse;
import com.bob.domain.post.service.dto.response.PostFileSummaryResponse;
import com.bob.domain.post.service.dto.response.PostMemberSummaryResponse;
import com.bob.domain.post.service.dto.response.PostsResponse;
import com.bob.domain.post.service.port.out.PostAreaPort;
import com.bob.domain.post.service.port.out.PostFilePort;
import com.bob.domain.post.service.port.out.PostMemberPort;
import com.bob.domain.post.service.reader.PostReader;
import com.bob.domain.post.usecase.PostDeleteUseCase;
import com.bob.domain.post.usecase.PostModifyUseCase;
import com.bob.domain.post.usecase.PostReadUseCase;
import com.bob.domain.post.usecase.PostWriteUseCase;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostService implements PostWriteUseCase, PostReadUseCase, PostModifyUseCase, PostDeleteUseCase {

  private final PostRepository postRepository;
  private final PostReader postReader;

  private final PostFavoriteService postFavoriteService;
  private final CategoryReader categoryReader;
  private final BookService bookService;

  private final PostMemberPort memberPort;
  private final PostAreaPort areaPort;
  private final PostFilePort filePort;

  @Transactional
  public PostCreateResponse createPostProcess(CreatePostCommand command) {
    PostAreaSummaryResponse areaSummary = areaPort.readPostAreaSummary(command.memberId());
    verifyAreaAuthentication(areaSummary.validity());
    Category category = categoryReader.readCategoryById(command.categoryId());
    Book book = bookService.createBookProcess(command.toBookCreateCommand());
    Post post = command.toPost(book, category, command.memberId(), areaSummary.emdId());
    postRepository.save(post);
    imageMapping(command.fileNames(), post.getId());
    return PostCreateResponse.of(post.getId());
  }

  private void verifyAreaAuthentication(boolean validity) {
    if (!validity) {
      throw new ApplicationException(NOT_VERIFIED_MEMBER);
    }
  }

  private void imageMapping(List<String> fileNames, Long postId) {
    if (fileNames == null || fileNames.isEmpty()) {
      return;
    }
    filePort.modifyReferenceId(fileNames, String.valueOf(postId));
  }

  @Transactional
  public void registerPostFavoriteProcess(RegisterPostFavoriteCommand command) {
    Post post = postReader.readPostById(command.postId());
    postFavoriteService.createPostFavoriteProcess(command.memberId(), post);
    postRepository.increaseFavoriteCount(post.getId());
  }

  @Transactional
  public void unregisterPostFavoriteProcess(RegisterPostFavoriteCommand command) {
    postFavoriteService.deletePostFavoriteProcess(command.memberId(), command.postId());
    postRepository.decreaseFavoriteCount(command.postId());
  }

  @Transactional(readOnly = true)
  public PostsResponse readFilteredPostsProcess(ReadFilteredPostsQuery query, Pageable pageable) {
    addChildCategoryIds(query);
    List<Post> posts = postReader.readFilteredPosts(query, pageable);
    Long totalCount = postRepository.countFilteredPosts(query);
    return PostsResponse.of(totalCount, posts);
  }

  private void addChildCategoryIds(ReadFilteredPostsQuery query) {
    if (query.categoryIds() == null || query.categoryIds().isEmpty()) {
      return;
    }
    List<Integer> categoryIds = categoryReader.readChildCategoryIds(query.categoryIds().get(0));
    query.updateCategoryIds(categoryIds);
  }

  @Transactional(readOnly = true)
  public PostsResponse readPostFavoritesProcess(ReadPostFavoritesQuery query, Pageable pageable) {
    PostFavoritesResponse response = postFavoriteService.readMemberFavoritePosts(query.memberId(), pageable);
    return new PostsResponse(response.totalCount(), response.postFavorites());
  }

  @Transactional
  public PostDetailResponse readPostDetailProcess(ReadPostDetailQuery query) {
    if (query.isClient()) {
      postRepository.increaseViewCount(query.postId());
    }
    Post post = postReader.readPostById(query.postId());
    verifyAccessiblePost(post, query.isClient());
    PostMemberSummaryResponse memberSummary = memberPort.readPostMemberSummary(post.getSellerId());
    PostFileSummaryResponse fileSummary = from(filePort.readPostFileSummaries(post.getId()));
    boolean isOwner = query.memberId() != null && post.getSellerId().equals(query.memberId());
    boolean isFavorite = postFavoriteService.isFavorite(query.memberId(), post.getId());
    return PostDetailResponse.from(post, memberSummary, fileSummary, isFavorite, isOwner);
  }

  private static void verifyAccessiblePost(Post post, boolean isClient) {
    if (isClient && (post.getStatus() == REMOVED || post.getStatus() == WITHHELD)) {
      throw new ApplicationException(ApplicationError.NOT_ACCESSIBLE_POST);
    }
  }

  @Transactional
  public void changePostProcess(ChangePostCommand command) {
    Post post = postReader.readPostById(command.postId());
    verifyPostOwner(command.memberId(), post.getSellerId());
    post.updateOptionalFields(command.sellPrice(), command.bookStatus(), command.description());
  }

  @Transactional
  public void changeTradeProgressProcess(ChangeTradeProgressCommand command) {
    Post post = postReader.readPostById(command.postId());
    post.updateTradeProgress(valueOf(command.status()));
  }

  @Transactional
  public void removePostProcess(RemovePostCommand command) {
    Post post = postReader.readPostById(command.postId());
    verifyPostOwner(command.memberId(), post.getSellerId());
    verifyRemovable(post);
    postFavoriteService.removePostFavoriteProcess(post.getId());
    post.updateStatus(REMOVED);
  }

  private static void verifyPostOwner(UUID requestMemberId, UUID postMemberId) {
    if (!Objects.equals(requestMemberId, postMemberId)) {
      throw new ApplicationException(NOT_POST_OWNER);
    }
  }

  private static void verifyRemovable(Post post) {
    if (post.getStatus() == REMOVED) {
      throw new ApplicationException(ALREADY_REMOVED_POST_STATE);
    }
    if (post.getTradeProgress() == IN_PROGRESS) {
      throw new ApplicationException(UNREMOVABLE_POST_STATE);
    }
  }

  @Transactional
  public void changeStatusByAccountEventProcess(ChangeMemberPostStatusCommand command) {
    postReader.readPostsByMember(command.memberId()).forEach(p -> {
      p.updateStatus(command.status());
      if (command.status() == REMOVED) {
        postFavoriteService.removePostFavoriteProcess(p.getId());
      }
    });
  }
}

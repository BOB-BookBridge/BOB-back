package com.bob.core.post.application;

import static com.bob.core.post.domain.status.Status.BANNED;
import static com.bob.core.post.domain.status.Status.DEACTIVATED;
import static com.bob.core.post.domain.status.Status.PENDING;
import static com.bob.global.exception.response.ApplicationError.POST_ACCESS_DENIED;
import static org.springframework.util.StringUtils.hasText;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.post.application.dto.query.ReadMemberPostsQuery;
import com.bob.core.post.application.dto.query.ReadPostDetailQuery;
import com.bob.core.post.application.dto.query.ReadPostFavoritesQuery;
import com.bob.core.post.application.dto.result.PostBasicInfo;
import com.bob.core.post.application.dto.result.PostDetail;
import com.bob.core.post.application.dto.result.PostSummaries;
import com.bob.core.post.application.dto.result.SearchPostsResult;
import com.bob.core.post.application.port.in.PostReader;
import com.bob.core.post.application.port.in.PostSearcher;
import com.bob.core.post.application.port.out.PostBookPort;
import com.bob.core.post.application.port.out.PostCategoryPort;
import com.bob.core.post.application.port.out.PostFilePort;
import com.bob.core.post.application.port.out.PostMemberPort;
import com.bob.core.post.application.port.result.PostBook;
import com.bob.core.post.application.port.result.PostFile;
import com.bob.core.post.application.port.result.PostMember;
import com.bob.core.post.domain.Post;
import com.bob.core.post.domain.repository.PostRepository;
import com.bob.core.post.domain.repository.dsl.query.ReadPostsQuery;
import com.bob.core.post.domain.repository.dsl.query.SearchManagementPostsQuery;
import com.bob.global.exception.exceptions.ApplicationException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostQueryService implements PostReader, PostSearcher {

    private final PostRepository postRepository;

    private final PostMemberPort memberPort;
    private final PostBookPort bookPort;
    private final PostFilePort filePort;

    private final PostCategoryPort categoryPort;

    @Override
    public List<Post> readByMember(ReadMemberPostsQuery query) {
        return postRepository.findAllByWriterId(query.memberId());
    }

    @Override
    public Post read(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id : " + postId));
    }

    @Override
    public PostBasicInfo readBasicInfo(Long postId) {
        Post post = read(postId);

        return PostBasicInfo.of(post);
    }

    @Override
    public PostSummaries readSummariesByQuery(ReadPostsQuery query, Pageable pageable) {
        addChildCategoryIds(query);
        addBookIds(query);

        List<Post> posts = postRepository.findFilteredPosts(query, pageable);
        Long totalCount = postRepository.countFilteredPosts(query);
        return PostSummaries.of(totalCount, posts, query.authenticatorId());
    }

    private void addChildCategoryIds(ReadPostsQuery query) {
        if (query.categoryIds() == null || query.categoryIds().isEmpty())
            return;

        List<Integer> categoryIds = categoryPort.readChildIds(query.categoryIds().get(0));

        query.updateCategoryIds(categoryIds);
    }

    private void addBookIds(ReadPostsQuery query) {
        if (query.bookIds() == null)
            return;

        List<Long> bookIds = null;
        if (hasText(query.keyword()))
            bookIds = bookPort.searchAllIds(String.valueOf(query.key()), query.keyword());

        query.updateBookIds(bookIds);
    }

    @Override
    public PostSummaries readFavoriteSummariesByQuery(ReadPostFavoritesQuery query, Pageable pageable) {
        List<Post> posts = postRepository.findPostsWithFavoriteByMemberId(query.memberId(), pageable);
        Long totalCount = postRepository.countPostsWithFavoriteByMemberId(query.memberId());
        return PostSummaries.of(totalCount, posts, query.memberId());
    }

    @Override
    @Transactional
    public PostDetail readDetail(Long postId, ReadPostDetailQuery query) {
        if (query.isClient())
            postRepository.increaseViewCount(postId);

        Post post = read(postId);

        verifyAccessiblePost(post, query.isClient(), query.memberId());

        PostBook book = bookPort.read(post.getBookId());
        PostMember member = memberPort.read(post.getWriterId());
        List<PostFile> files = filePort.readPostFiles(post.getId());

        boolean isOwner = query.memberId() != null && post.getWriterId().equals(query.memberId());

        boolean isFavorite = query.memberId() != null && post.isFavorite(query.memberId());

        return PostDetail.of(post, book, member, files, isFavorite, isOwner);
    }

    private static void verifyAccessiblePost(Post post, boolean isClient, UUID memberId) {
        if (!isClient)
            return;

        boolean isOwner = post.getWriterId().equals(memberId);
        if (isOwner)
            return;

        if (post.getStatus() == DEACTIVATED || post.getStatus() == BANNED || post.getStatus() == PENDING)
            throw new ApplicationException(POST_ACCESS_DENIED);
    }

    @Override
    public SearchPostsResult searchByQuery(SearchManagementPostsQuery query, Pageable pageable) {
        List<Post> posts = postRepository.searchPosts(query, pageable);
        Long totalCount = postRepository.countSearchedPosts(query);

        return new SearchPostsResult(totalCount, posts);
    }
}

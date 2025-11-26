package com.bob.core.domain.post.repository.dsl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.bob.core.application.post.dto.query.ReadPostsQuery;
import com.bob.core.domain.post.Post;

public interface CustomPostRepository {

    List<Post> findFilteredPosts(ReadPostsQuery query, Pageable pageable);

    Long countFilteredPosts(ReadPostsQuery query);

    List<Post> findPostsWithFavoriteByMemberId(UUID memberId, Pageable pageable);

    Long countPostsWithFavoriteByMemberId(UUID memberId);
}

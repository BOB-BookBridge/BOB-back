package com.bob.core.domain.post.repository.dsl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.bob.core.domain.post.Post;
import com.bob.core.domain.post.repository.dsl.query.ReadPostsQuery;

public interface CustomPostRepository {

    List<Post> findFilteredPosts(ReadPostsQuery query, Pageable pageable);

    Long countFilteredPosts(ReadPostsQuery query);

    List<Post> findPostsWithFavoriteByMemberId(UUID memberId, Pageable pageable);

    Long countPostsWithFavoriteByMemberId(UUID memberId);
}

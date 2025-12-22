package com.bob.core.post.domain.repository.dsl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.bob.core.post.domain.Post;
import com.bob.core.post.domain.repository.dsl.query.ReadPostsQuery;

public interface CustomPostRepository {

    List<Post> findFilteredPosts(ReadPostsQuery query, Pageable pageable);

    Long countFilteredPosts(ReadPostsQuery query);

    List<Post> findPostsWithFavoriteByMemberId(UUID memberId, Pageable pageable);

    Long countPostsWithFavoriteByMemberId(UUID memberId);
}

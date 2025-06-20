package com.bob.domain.post.usecase;

import com.bob.domain.post.service.dto.query.ReadFilteredPostsQuery;
import com.bob.domain.post.service.dto.query.ReadPostDetailQuery;
import com.bob.domain.post.service.dto.query.ReadPostFavoritesQuery;
import com.bob.domain.post.service.dto.response.PostDetailResponse;
import com.bob.domain.post.service.dto.response.PostsResponse;
import org.springframework.data.domain.Pageable;

public interface PostReadUseCase {

  PostsResponse readFilteredPostsProcess(ReadFilteredPostsQuery query, Pageable pageable);

  PostsResponse readPostFavoritesProcess(ReadPostFavoritesQuery query, Pageable pageable);

  PostDetailResponse readPostDetailProcess(ReadPostDetailQuery query);
}

package com.bob.core.post.application.port.in;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.bob.core.post.application.dto.query.ReadMemberPostsQuery;
import com.bob.core.post.application.dto.query.ReadPostDetailQuery;
import com.bob.core.post.application.dto.query.ReadPostFavoritesQuery;
import com.bob.core.post.application.dto.result.PostDetail;
import com.bob.core.post.application.dto.result.PostSummaries;
import com.bob.core.post.domain.Post;
import com.bob.core.post.domain.repository.dsl.query.ReadPostsQuery;

public interface PostReader {

    Post read(Long postId);

    List<Post> readByMember(ReadMemberPostsQuery query);

    PostSummaries readSummariesByQuery(ReadPostsQuery query, Pageable pageable);

    PostSummaries readFavoriteSummariesByQuery(ReadPostFavoritesQuery query, Pageable pageable);

    PostDetail readDetail(Long postId, ReadPostDetailQuery query);
}

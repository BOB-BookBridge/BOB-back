package com.bob.core.application.post.port.in;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.bob.core.application.post.dto.query.ReadMemberPostsQuery;
import com.bob.core.application.post.dto.query.ReadPostDetailQuery;
import com.bob.core.application.post.dto.query.ReadPostFavoritesQuery;
import com.bob.core.application.post.dto.result.PostDetail;
import com.bob.core.application.post.dto.result.PostSummaries;
import com.bob.core.domain.post.Post;
import com.bob.core.domain.post.repository.dsl.query.ReadPostsQuery;

public interface PostReader {

    Post read(Long postId);

    List<Post> readByMember(ReadMemberPostsQuery query);

    PostSummaries readSummariesByQuery(ReadPostsQuery query, Pageable pageable);

    PostSummaries readFavoriteSummariesByQuery(ReadPostFavoritesQuery query, Pageable pageable);

    PostDetail readDetail(Long postId, ReadPostDetailQuery query);
}

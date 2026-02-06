package com.bob.core.post.application.port.in;

import org.springframework.data.domain.Pageable;

import com.bob.core.post.application.dto.result.SearchPostsResult;
import com.bob.core.post.domain.repository.dsl.query.SearchManagementPostsQuery;

public interface PostSearcher {

    SearchPostsResult searchByQuery(SearchManagementPostsQuery query, Pageable pageable);
}

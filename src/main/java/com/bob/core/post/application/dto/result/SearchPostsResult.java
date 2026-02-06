package com.bob.core.post.application.dto.result;

import java.util.List;

import com.bob.core.post.domain.Post;

public record SearchPostsResult(Long totalCount, List<Post> posts) {

}

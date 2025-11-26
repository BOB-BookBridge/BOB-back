package com.bob.core.adapter.post.api.response;

import com.bob.core.domain.post.Post;

public record CreatePostResponse(Long id) {

    public static CreatePostResponse of(Post post) {
        return new CreatePostResponse(post.getId());
    }
}

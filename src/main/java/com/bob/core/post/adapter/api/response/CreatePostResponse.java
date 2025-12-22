package com.bob.core.post.adapter.api.response;

import com.bob.core.post.domain.Post;

public record CreatePostResponse(Long id) {

    public static CreatePostResponse of(Post post) {
        return new CreatePostResponse(post.getId());
    }
}

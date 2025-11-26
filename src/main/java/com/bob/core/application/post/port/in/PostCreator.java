package com.bob.core.application.post.port.in;

import com.bob.core.application.post.dto.command.CreatePostCommand;
import com.bob.core.domain.post.Post;

public interface PostCreator {

    Post create(CreatePostCommand command);
}


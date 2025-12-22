package com.bob.core.post.application.port.in;

import com.bob.core.post.application.dto.command.CreatePostCommand;
import com.bob.core.post.domain.Post;

public interface PostCreator {

    Post create(CreatePostCommand command);
}


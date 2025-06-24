package com.bob.domain.post.usecase;

import com.bob.domain.post.service.dto.command.CreatePostCommand;
import com.bob.domain.post.service.dto.command.RegisterPostFavoriteCommand;
import com.bob.domain.post.service.dto.response.PostCreateResponse;

public interface PostWriteUseCase {

  PostCreateResponse createPostProcess(CreatePostCommand command);

  void registerPostFavoriteProcess(RegisterPostFavoriteCommand command);
}


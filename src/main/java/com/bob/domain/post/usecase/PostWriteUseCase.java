package com.bob.domain.post.usecase;

import com.bob.domain.post.service.dto.command.CreatePostCommand;
import com.bob.domain.post.service.dto.command.RegisterPostFavoriteCommand;

public interface PostWriteUseCase {

  void createPostProcess(CreatePostCommand command);

  void registerPostFavoriteProcess(RegisterPostFavoriteCommand command);
}


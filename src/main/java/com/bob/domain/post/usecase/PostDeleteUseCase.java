package com.bob.domain.post.usecase;

import com.bob.domain.post.service.dto.command.RegisterPostFavoriteCommand;
import com.bob.domain.post.service.dto.command.RemovePostCommand;

public interface PostDeleteUseCase {

  void unregisterPostFavoriteProcess(RegisterPostFavoriteCommand command);

  void removePostProcess(RemovePostCommand command);
}

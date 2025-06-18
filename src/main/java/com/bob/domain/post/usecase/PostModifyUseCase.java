package com.bob.domain.post.usecase;

import com.bob.domain.post.service.dto.command.ChangePostCommand;

public interface PostModifyUseCase {
  void changePostProcess(ChangePostCommand command);
}

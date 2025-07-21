package com.bob.domain.post.usecase;

import com.bob.domain.post.service.dto.command.ChangePostCommand;
import com.bob.domain.post.service.dto.command.ChangePostStatusCommand;

public interface PostModifyUseCase {

  void changePostProcess(ChangePostCommand command);

  void changePostStatusProcess(ChangePostStatusCommand command);
}

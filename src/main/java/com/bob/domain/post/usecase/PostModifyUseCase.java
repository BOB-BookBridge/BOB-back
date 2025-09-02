package com.bob.domain.post.usecase;

import com.bob.domain.post.service.dto.command.ChangePostCommand;
import com.bob.domain.post.service.dto.command.ChangeTradeProgressCommand;

public interface PostModifyUseCase {

  void changePostProcess(ChangePostCommand command);

  void changeTradeProgressProcess(ChangeTradeProgressCommand command);
}

package com.bob.web.file.adapter.in;

import com.bob.domain.file.service.dto.command.ModifyReferenceIdCommand;
import com.bob.domain.file.usecase.FileModifyUseCase;
import com.bob.domain.post.service.port.out.PostFilePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostFileAdapter implements PostFilePort {

  private final FileModifyUseCase modifyUseCase;

  @Override
  public void modifyReferenceId(String oldRefId, Long newRefId) {
    ModifyReferenceIdCommand command = new ModifyReferenceIdCommand(oldRefId, newRefId);
    modifyUseCase.modifyReferenceIdProcess(command);
  }
}

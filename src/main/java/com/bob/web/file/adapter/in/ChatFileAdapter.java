package com.bob.web.file.adapter.in;

import com.bob.domain.chat.service.port.out.ChatFilePort;
import com.bob.domain.file.service.dto.command.ModifyReferenceIdCommand;
import com.bob.domain.file.usecase.FileModifyUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChatFileAdapter implements ChatFilePort {

  private final FileModifyUseCase modifyUseCase;

  @Override
  public void modifyReferenceId(List<String> fileNames, String domainId) {
    ModifyReferenceIdCommand command = new ModifyReferenceIdCommand(fileNames, domainId);
    modifyUseCase.modifyReferenceIdProcess(command);
  }
}

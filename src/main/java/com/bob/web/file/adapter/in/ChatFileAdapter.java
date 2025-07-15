package com.bob.web.file.adapter.in;

import com.bob.domain.chat.service.port.out.ChatFilePort;
import com.bob.domain.file.service.dto.command.ModifyReferenceIdCommand;
import com.bob.domain.file.service.dto.query.ReadFilesWithDomainIdQuery;
import com.bob.domain.file.service.dto.response.FilesResponse;
import com.bob.domain.file.usecase.FileModifyUseCase;
import com.bob.domain.file.usecase.FileReadUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChatFileAdapter implements ChatFilePort {

  private final FileReadUseCase readUseCase;
  private final FileModifyUseCase modifyUseCase;

  @Override
  public FilesResponse readChatFileSummaries(Long chatRoomId) {
    ReadFilesWithDomainIdQuery query = new ReadFilesWithDomainIdQuery(chatRoomId.toString());
    return readUseCase.readFilesByDomainId(query);
  }

  @Override
  public void modifyReferenceId(List<String> fileNames, String domainId) {
    ModifyReferenceIdCommand command = new ModifyReferenceIdCommand(fileNames, domainId);
    modifyUseCase.modifyReferenceIdProcess(command);
  }
}

package com.bob.web.file.adapter.in;

import com.bob.domain.file.entity.type.FileDomain;
import com.bob.domain.file.service.dto.command.ModifyReferenceIdCommand;
import com.bob.domain.file.service.dto.query.ReadFilesWithDomainIdQuery;
import com.bob.domain.file.service.dto.response.FilesResponse;
import com.bob.domain.file.usecase.FileModifyUseCase;
import com.bob.domain.file.usecase.FileReadUseCase;
import com.bob.domain.post.service.port.out.PostFilePort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostFileAdapter implements PostFilePort {

  private final FileReadUseCase readUseCase;
  private final FileModifyUseCase modifyUseCase;

  @Override
  public FilesResponse readPostFileSummaries(Long postId) {
    ReadFilesWithDomainIdQuery query = new ReadFilesWithDomainIdQuery(FileDomain.POST, postId.toString());
    return readUseCase.readFilesByDomainId(query);
  }

  @Override
  public void modifyReferenceId(List<String> fileNames, String domainId) {
    ModifyReferenceIdCommand command = new ModifyReferenceIdCommand(fileNames, domainId);
    modifyUseCase.modifyReferenceIdProcess(command);
  }
}

package com.bob.web.file.adapter.in;

import com.bob.domain.file.service.dto.command.ModifyReferenceIdCommand;
import com.bob.domain.file.service.dto.query.ReadFilesWithDomainIdQuery;
import com.bob.domain.file.service.dto.response.FilesResponse;
import com.bob.domain.file.usecase.FileModifyUseCase;
import com.bob.domain.file.usecase.FileReadUseCase;
import com.bob.domain.post.service.dto.response.PostFileSummaryResponse;
import com.bob.domain.post.service.dto.response.PostFileSummaryResponse.FileSummary;
import com.bob.domain.post.service.port.out.PostFilePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostFileAdapter implements PostFilePort {

  private final FileReadUseCase readUseCase;
  private final FileModifyUseCase modifyUseCase;

  @Override
  public PostFileSummaryResponse readPostFileSummaries(Long postId) {
    ReadFilesWithDomainIdQuery query = new ReadFilesWithDomainIdQuery(postId.toString());
    FilesResponse response = readUseCase.readFilesByDomainId(query);
    return PostFileSummaryResponse.builder()
        .images(response.summaries().stream()
            .map(summary -> FileSummary.of(summary.sequence(), summary.fileName()))
            .toList())
        .build();
  }

  @Override
  public void modifyReferenceId(String oldRefId, Long newRefId) {
    ModifyReferenceIdCommand command = new ModifyReferenceIdCommand(oldRefId, newRefId);
    modifyUseCase.modifyReferenceIdProcess(command);
  }
}

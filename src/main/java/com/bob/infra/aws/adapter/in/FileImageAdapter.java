package com.bob.infra.aws.adapter.in;

import com.bob.domain.file.service.port.FileS3Port;
import com.bob.infra.aws.service.usecase.FileDeleteUseCase;
import com.bob.infra.aws.service.usecase.FileReadUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FileImageAdapter implements FileS3Port {

  private final FileReadUseCase readUseCase;
  private final FileDeleteUseCase deleteUseCase;

  @Override
  public List<String> generateFileUploadUrlsProcess(List<String> fileNames, List<String> contentTypes) {
    return readUseCase.generateFileUploadUrlProcess(fileNames, contentTypes);
  }

  @Override
  public void removeUnusedFilesProcess(List<String> fileNames) {
    deleteUseCase.removeFilesProcess(fileNames);
  }
}

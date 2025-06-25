package com.bob.infra.aws.adapter.in;

import com.bob.domain.file.service.port.FileImagePort;
import com.bob.infra.aws.service.usecase.ImageUrlReadUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FileImageAdapter implements FileImagePort {

  private final ImageUrlReadUseCase readUseCase;

  @Override
  public List<String> generateFileUploadUrlsProcess(List<String> fileNames, List<String> contentTypes) {
    return readUseCase.generateImageUploadUrlProcess(fileNames, contentTypes);
  }
}

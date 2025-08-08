package com.bob.infra.aws.service.usecase;

import java.util.List;

public interface FileReadUseCase {

  List<String> generateFileUploadUrlProcess(List<String> fileNames, List<String> contentTypes);
}

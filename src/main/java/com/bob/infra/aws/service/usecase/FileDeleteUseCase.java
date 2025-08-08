package com.bob.infra.aws.service.usecase;

import java.util.List;

public interface FileDeleteUseCase {

  void removeFilesProcess(List<String> fileNames);
}

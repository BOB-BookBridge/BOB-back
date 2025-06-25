package com.bob.infra.aws.service.usecase;

import java.util.List;

public interface ImageUrlReadUseCase {

  List<String> generateImageUploadUrlProcess(List<String> fileNames, List<String> contentTypes);
}

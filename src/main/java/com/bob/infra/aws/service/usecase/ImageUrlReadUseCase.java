package com.bob.infra.aws.service.usecase;

import java.util.List;

public interface ImageUrlReadUseCase {

  String generateSingleImageUploadUrlProcess(String fileName, String contentType);

  List<String> generateMultiImageUploadUrlProcess(List<String> fileNames, List<String> contentTypes);
}

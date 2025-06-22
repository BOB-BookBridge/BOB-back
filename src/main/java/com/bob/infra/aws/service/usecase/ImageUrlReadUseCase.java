package com.bob.infra.aws.service.usecase;

public interface ImageUrlReadUseCase {

  String generateImageUploadUrlProcess(String imageName, String contentType);
}

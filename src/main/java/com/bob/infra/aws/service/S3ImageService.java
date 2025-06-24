package com.bob.infra.aws.service;

import com.bob.infra.aws.service.usecase.ImageUrlReadUseCase;
import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@RequiredArgsConstructor
@Service
public class S3ImageService implements ImageUrlReadUseCase {

  private final S3Presigner signer;

  @Value("${spring.cloud.aws.s3.bucket}")
  private String bucketName;

  public String generateSingleImageUploadUrlProcess(String fileName, String contentType) {
    PutObjectRequest putObjectRequest = createPutObjectRequest(fileName, contentType);
    PutObjectPresignRequest preSignRequest = createPutObjectPresignRequest(putObjectRequest);
    PresignedPutObjectRequest putRequest = signer.presignPutObject(preSignRequest);
    return putRequest.url().toString();
  }

  public List<String> generateMultiImageUploadUrlProcess(List<String> fileNames, List<String> contentTypes) {
    return IntStream.range(0, fileNames.size())
        .mapToObj(idx -> generateSingleImageUploadUrlProcess(fileNames.get(idx), contentTypes.get(idx)))
        .toList();
  }

  private PutObjectPresignRequest createPutObjectPresignRequest(PutObjectRequest putObjectRequest) {
    return PutObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(1))
        .putObjectRequest(putObjectRequest)
        .build();
  }

  private PutObjectRequest createPutObjectRequest(String key, String contentType) {
    return PutObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .contentType(contentType)
        .build();
  }
}

package com.bob.infra.aws.service;

import com.bob.infra.aws.service.usecase.ImageUrlReadUseCase;
import java.time.Duration;
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

  public String generateImageUploadUrlProcess(String key, String contentType) {
    PutObjectRequest putObjectRequest = createPutObjectRequest(key, contentType);
    PutObjectPresignRequest preSignRequest = createPutObjectPresignRequest(putObjectRequest);
    PresignedPutObjectRequest putRequest = signer.presignPutObject(preSignRequest);
    return putRequest.url().toString();
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

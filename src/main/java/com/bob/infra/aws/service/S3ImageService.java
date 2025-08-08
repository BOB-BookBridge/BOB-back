package com.bob.infra.aws.service;

import com.bob.infra.aws.service.usecase.FileDeleteUseCase;
import com.bob.infra.aws.service.usecase.FileReadUseCase;
import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3ImageService implements FileReadUseCase, FileDeleteUseCase {

  private final S3Client client;
  private final S3Presigner signer;

  @Value("${spring.cloud.aws.s3.bucket}")
  private String bucketName;

  public String issuePresignedImageUploadUrlProcess(String fileName, String contentType) {
    PutObjectRequest putObjectRequest = createPutObjectRequest(fileName, contentType);
    PutObjectPresignRequest preSignRequest = createPutObjectPresignRequest(putObjectRequest);
    PresignedPutObjectRequest putRequest = signer.presignPutObject(preSignRequest);
    return putRequest.url().toString();
  }

  public List<String> generateFileUploadUrlProcess(List<String> fileNames, List<String> contentTypes) {
    return IntStream.range(0, fileNames.size())
        .mapToObj(idx -> issuePresignedImageUploadUrlProcess(fileNames.get(idx), contentTypes.get(idx)))
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

  @Override
  public void removeFilesProcess(List<String> fileNames) {
    fileNames.forEach(fileName -> {
      try {
        client.deleteObject(builder -> builder
            .bucket(bucketName)
            .key(fileName)
        );
      } catch (Exception e) {
        log.warn("failed to delete file from S3: {}", fileName, e);
      }
    });
  }
}

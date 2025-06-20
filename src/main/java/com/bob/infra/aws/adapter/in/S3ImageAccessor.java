package com.bob.infra.aws.adapter.in;

import com.bob.domain.member.service.port.out.MemberProfileImageAccessor;
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
public class S3ImageAccessor implements MemberProfileImageAccessor {

  private final S3Presigner signer;

  @Value("${spring.cloud.aws.s3.bucket}")
  private String bucketName;

  @Override
  public String getImageUploadUrl(String imageName, String contentType) {
    PutObjectRequest putObjectRequest = createPutObjectRequest(imageName, contentType);
    PutObjectPresignRequest putObjectPresignRequest = createPutObjectPresignRequest(putObjectRequest);
    PresignedPutObjectRequest presignedPutObjectRequest = signer.presignPutObject(putObjectPresignRequest);
    return presignedPutObjectRequest.url().toString();
  }

  private PutObjectPresignRequest createPutObjectPresignRequest(PutObjectRequest putObjectRequest) {
    return PutObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(1))
        .putObjectRequest(putObjectRequest)
        .build();
  }

  private PutObjectRequest createPutObjectRequest(String imageName, String contentType) {
    return PutObjectRequest.builder()
        .bucket(bucketName)
        .key(imageName)
        .contentType(contentType)
        .build();
  }
}

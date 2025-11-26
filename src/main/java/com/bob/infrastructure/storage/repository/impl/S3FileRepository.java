package com.bob.infrastructure.storage.repository.impl;

import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bob.infrastructure.storage.repository.FileRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3FileRepository implements FileRepository {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    private final S3Client client;
    private final S3Presigner presigner;

    @Override
    public List<String> generateUploadUrls(List<String> fileNames, List<String> contentTypes) {
        return IntStream.range(0, fileNames.size())
            .mapToObj(idx -> generateUploadUrl(fileNames.get(idx), contentTypes.get(idx)))
            .toList();
    }

    @Override
    public String generateUploadUrl(String fileName, String contentType) {
        PutObjectRequest putObjectRequest = createPutObjectRequest(fileName, contentType);
        PutObjectPresignRequest preSignRequest = createPutObjectPresignRequest(putObjectRequest);
        PresignedPutObjectRequest putRequest = presigner.presignPutObject(preSignRequest);
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

    @Override
    public void deleteFiles(List<String> fileNames) {
        fileNames.forEach(this::deleteFile);
    }

    private void deleteFile(String fileName) {
        try {
            client.deleteObject(builder -> builder.bucket(bucketName).key(fileName));
            log.debug("Successfully deleted file from S3: fileName={}, bucket={}", fileName, bucketName);
        } catch (Exception e) {
            log.warn("Failed to delete file from S3: fileName={}, bucket={}", fileName, bucketName, e);
        }
    }
}
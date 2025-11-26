package com.bob.infrastructure.config;

import static software.amazon.awssdk.auth.credentials.StaticCredentialsProvider.create;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsS3Config {

    @Value("${spring.cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @Bean
    public AwsCredentials awsCredentials() {
        return AwsBasicCredentials.create(accessKey, secretKey);
    }

    @Bean
    public S3Presigner s3Presigner(AwsCredentials credentials) {
        return S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(create(credentials))
            .build();
    }

    @Bean
    public S3Client s3Client(AwsCredentials credentials) {
        return S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(create(credentials))
            .build();
    }
}

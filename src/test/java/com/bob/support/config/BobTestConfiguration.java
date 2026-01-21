package com.bob.support.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import org.mockito.Mockito;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.bob.core.file.application.port.out.infra.FileStoragePort;
import com.bob.core.inquiry.application.port.out.infra.InquiryMailSender;
import com.bob.core.member.application.port.out.infra.MemberAuthMailSender;

@TestConfiguration
public class BobTestConfiguration {

    @Bean
    @Primary
    public MemberAuthMailSender memberAuthMailSender() {
        return new MemberAuthMailSender() {
            @Override
            public void sendAuthCode(String email, String code) {
                System.out.println("Auth code email sent to: " + email);
            }

            @Override
            public void sendTempPassword(String email, String password) {
                System.out.println("Temp password email sent to: " + email);
            }
        };
    }

    @Bean
    @Primary
    public InquiryMailSender inquiryMailSender() {
        return new InquiryMailSender() {
            @Override
            public void sendInquiryReply(String email, String content, String reply) {
                System.out.println("Inquiry reply email sent to: " + email);
            }
        };
    }

    @Bean
    @Primary
    public S3Presigner s3Presigner() {
        S3Presigner presigner = Mockito.mock(S3Presigner.class);
        PresignedPutObjectRequest mockPresignedRequest = Mockito.mock(PresignedPutObjectRequest.class);
        try {
            given(mockPresignedRequest.url()).willReturn(
                new URL("https://mock-url.com/test-bucket/test.png?X-Amz-Algorithm=AWS4-HMAC-SHA256"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        given(presigner.presignPutObject(any(PutObjectPresignRequest.class))).willReturn(mockPresignedRequest);
        return presigner;
    }

    @Bean
    @Primary
    public ApplicationEventPublisher applicationEventPublisher(ApplicationContext context) {
        return event -> {
            System.out.println("event publish success - event type: " + event.getClass().getSimpleName());
            context.publishEvent(event);
        };
    }

    @Bean
    @Primary
    public FileStoragePort fileStoragePort() {
        return new TestFileStoragePort();
    }

    public static class TestFileStoragePort implements FileStoragePort {

        @Getter
        private final List<String> deletedFiles = new ArrayList<>();

        @Override
        public List<String> generateUploadUrls(List<String> fileNames, List<String> contentTypes) {
            return fileNames.stream().map(name -> "https://mock-url.com/" + name).toList();
        }

        @Override
        public void deleteFiles(List<String> fileNames) {
            deletedFiles.addAll(fileNames);
        }

        public void clearDeletedFiles() {
            deletedFiles.clear();
        }
    }
}

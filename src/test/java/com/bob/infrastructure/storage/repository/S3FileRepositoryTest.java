package com.bob.infrastructure.storage.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import org.springframework.test.util.ReflectionTestUtils;

import com.bob.infrastructure.storage.repository.impl.S3FileRepository;

@DisplayName("S3 파일 저장소 테스트")
@ExtendWith(MockitoExtension.class)
class S3FileRepositoryTest {

    @InjectMocks
    private S3FileRepository fileRepository;

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner presigner;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileRepository, "bucketName", "test-bucket");
    }

    @Test
    void 업로드_URL_단일_생성() {
        String fileName = "profile/test.png";
        String contentType = "image/png";

        PresignedPutObjectRequest mockPresignedRequest = org.mockito.Mockito.mock(PresignedPutObjectRequest.class);
        given(mockPresignedRequest.url()).willReturn(org.mockito.Mockito.mock(URL.class));
        given(presigner.presignPutObject(any(PutObjectPresignRequest.class))).willReturn(mockPresignedRequest);

        String result = fileRepository.generateUploadUrl(fileName, contentType);

        assertThat(result).isNotEmpty();
        then(presigner).should().presignPutObject(any(PutObjectPresignRequest.class));
    }

    @Test
    void 업로드_URL_목록_생성() {
        List<String> fileNames = List.of("img1.jpg", "img2.jpg");
        List<String> contentTypes = List.of("image/jpeg", "image/jpeg");

        PresignedPutObjectRequest mockPresignedRequest = org.mockito.Mockito.mock(PresignedPutObjectRequest.class);
        given(mockPresignedRequest.url()).willReturn(org.mockito.Mockito.mock(URL.class));
        given(presigner.presignPutObject(any(PutObjectPresignRequest.class))).willReturn(mockPresignedRequest);

        List<String> result = fileRepository.generateUploadUrls(fileNames, contentTypes);

        assertThat(result).hasSize(2);
        then(presigner).should(times(2)).presignPutObject(any(PutObjectPresignRequest.class));
    }

    @Test
    void 파일_삭제() {
        List<String> fileNames = List.of("a.png", "b.jpg");

        fileRepository.deleteFiles(fileNames);

        then(s3Client).should(times(2)).deleteObject(any(Consumer.class));
    }

    @Test
    void 파일_삭제_실패_시_로깅() {
        given(s3Client.deleteObject(any(Consumer.class))).willThrow(new RuntimeException("S3 deletion failed"));

        Logger logger = (Logger)LoggerFactory.getLogger(S3FileRepository.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        fileRepository.deleteFiles(List.of("test-file.png"));

        ILoggingEvent event = appender.list.get(0);
        assertThat(event.getFormattedMessage()).contains("Failed to delete file from S3");
        assertThat(event.getThrowableProxy()).isNotNull();
        assertThat(event.getThrowableProxy().getMessage()).contains("S3 deletion failed");

        logger.detachAppender(appender);
        appender.stop();
    }
}

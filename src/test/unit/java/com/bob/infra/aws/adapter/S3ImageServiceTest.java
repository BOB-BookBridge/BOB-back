package com.bob.infra.aws.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.bob.infra.aws.service.S3ImageService;
import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@DisplayName("S3 Presigned URL 생성 테스트")
@ExtendWith(MockitoExtension.class)
class S3ImageServiceTest {

  @InjectMocks
  private S3ImageService imageService;

  @Mock
  private S3Client client;

  @Mock
  private S3Presigner signer;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(imageService, "bucketName", "bucket");
  }

  @Test
  @DisplayName("Presigned PUT URL 생성 테스트")
  void presignedPutUrl을_생성할_수_있다() throws MalformedURLException {
    // given
    String fileName = "profile/test.png";
    String contentType = "image/png";
    URI uri = URI.create("https://dummy-s3.com/" + fileName);

    PresignedPutObjectRequest presignedRequest = mock(PresignedPutObjectRequest.class);
    given(presignedRequest.url()).willReturn(uri.toURL());
    given(signer.presignPutObject(any(PutObjectPresignRequest.class))).willReturn(presignedRequest);

    // when
    String result = imageService.issuePresignedImageUploadUrlProcess(fileName, contentType);

    // then
    assertThat(result).isEqualTo(uri.toString());
    verify(signer, times(1)).presignPutObject(any(PutObjectPresignRequest.class));
  }

  @DisplayName("다중 Presigned PUT URL 생성 테스트")
  @Test
  void 다중_업로드_요청에_대한_URL들을_생성할_수_있다() throws MalformedURLException {
    // given
    List<String> fileNames = List.of("img1.jpg", "img2.jpg");
    List<String> contentTypes = List.of("image/jpeg", "image/jpeg");
    URI dummyUrl1 = URI.create("https://dummy-s3.com/" + fileNames.get(0));
    URI dummyUrl2 = URI.create("https://dummy-s3.com/" + fileNames.get(1));
    PresignedPutObjectRequest presigned1 = mock(PresignedPutObjectRequest.class);
    PresignedPutObjectRequest presigned2 = mock(PresignedPutObjectRequest.class);
    given(presigned1.url()).willReturn(dummyUrl1.toURL());
    given(presigned2.url()).willReturn(dummyUrl2.toURL());
    given(signer.presignPutObject(any(PutObjectPresignRequest.class))).willReturn(presigned1, presigned2);

    // when
    List<String> result = imageService.generateFileUploadUrlProcess(fileNames, contentTypes);

    // then
    assertThat(result).containsExactly(
        dummyUrl1.toString(),
        dummyUrl2.toString()
    );
    verify(signer, times(2)).presignPutObject(any(PutObjectPresignRequest.class));
  }

  @DisplayName("PutObjectRequest, PresignRequest 생성 로직 테스트")
  @Test
  void 파일저장_URL생성_요청과_파일저장_요청을_할_수_있다() {
    // given
    String key = "test.jpg";
    String contentType = "image/jpeg";

    // when
    PutObjectRequest request = ReflectionTestUtils.invokeMethod(imageService, "createPutObjectRequest", key, contentType);
    PutObjectPresignRequest presignRequest = ReflectionTestUtils.invokeMethod(imageService, "createPutObjectPresignRequest", request);

    // then
    assertThat(request.bucket()).isEqualTo("bucket");
    assertThat(request.key()).isEqualTo("test.jpg");
    assertThat(request.contentType()).isEqualTo("image/jpeg");

    assertThat(presignRequest.putObjectRequest()).isEqualTo(request);
    assertThat(presignRequest.signatureDuration()).isEqualTo(Duration.ofMinutes(1));
  }

  @DisplayName("S3 파일 삭제 테스트")
  @Test
  void 파일들을_S3에서_삭제할_수_있다() {
    // given
    List<String> fileNames = List.of("a.png", "b.jpg");

    // when
    imageService.removeFilesProcess(fileNames);

    // then
    verify(client, times(fileNames.size())).deleteObject(any(Consumer.class));
  }
}

package com.bob.infra.aws.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.bob.infra.aws.adapter.in.S3ImageAccessor;
import java.net.MalformedURLException;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@DisplayName("S3 Presigned URL 생성 테스트")
@ExtendWith(MockitoExtension.class)
class S3AccessorTest {

  @Mock
  private S3Presigner signer;

  @InjectMocks
  private S3ImageAccessor s3Accessor;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(s3Accessor, "bucketName", "bucket");
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
    String result = s3Accessor.getImageUploadUrl(fileName, contentType);

    // then
    assertThat(result).isEqualTo(uri.toString());
    verify(signer, times(1)).presignPutObject(any(PutObjectPresignRequest.class));
  }
}

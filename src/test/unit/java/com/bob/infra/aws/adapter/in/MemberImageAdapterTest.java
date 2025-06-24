package com.bob.infra.aws.adapter.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.bob.infra.aws.service.usecase.ImageUrlReadUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("MemberImageAdapter 테스트")
@ExtendWith(MockitoExtension.class)
class MemberImageAdapterTest {

  @Mock
  private ImageUrlReadUseCase readUseCase;

  @InjectMocks
  private MemberImageAdapter adapter;

  @Test
  @DisplayName("회원 프로필 이미지 업로드 URL 생성 테스트")
  void 회원_프로필_이미지_업로드_URL을_생성할_수_있다() {
    // given
    String fileName = "profile/test.jpg";
    String contentType = "image/jpeg";
    String expectedUrl = "https://example.com/profile/test.jpg";
    given(readUseCase.generateSingleImageUploadUrlProcess(fileName, contentType)).willReturn(expectedUrl);

    // when
    String result = adapter.generateMemberProfileImageUploadUrl(fileName, contentType);

    // then
    assertThat(result).isEqualTo(expectedUrl);
  }
}
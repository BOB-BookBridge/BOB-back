package com.bob.global.utils.image;

import static com.bob.global.exception.response.ApplicationError.UN_SUPPORTED_DOMAIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.bob.global.exception.exceptions.ApplicationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("이미지 디렉토리 유틸 테스트")
class ImageDirectoryTest {

  @Test
  @DisplayName("ImageDirectory 조회 테스트")
  void 문자열로부터_ImageDirectory를_조회할_수_있다() {
    assertThat(ImageDirectory.from("profile")).isEqualTo(ImageDirectory.PROFILE);
    assertThat(ImageDirectory.from("post")).isEqualTo(ImageDirectory.POST);
    assertThat(ImageDirectory.from("chat")).isEqualTo(ImageDirectory.CHAT);
  }

  @Test
  @DisplayName("지원하지 않는 도메인 예외 발생 테스트")
  void 존재하지_않는_도메인이면_예외를_던진다() {
    assertThatThrownBy(() -> ImageDirectory.from("invalid"))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(UN_SUPPORTED_DOMAIN.getMessage());
  }
}
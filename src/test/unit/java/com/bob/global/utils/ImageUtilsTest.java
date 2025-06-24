package com.bob.global.utils;

import static com.bob.global.utils.image.ImageDirectory.PROFILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import com.bob.global.utils.image.ImageUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("이미지 Util 테스트")
class ImageUtilsTest {

  @Test
  @DisplayName("Content-Type 기반 파일 확장자 추출 테스트")
  void contentType으로_확장자를_추출할_수_있다() {
    assertThat(ImageUtils.extractExtension("image/jpeg")).isEqualTo("jpg");
    assertThat(ImageUtils.extractExtension("image/png")).isEqualTo("png");
    assertThat(ImageUtils.extractExtension("image/gif")).isEqualTo("gif");
  }

  @Test
  @DisplayName("지원하지 않는 Content-Type 테스트")
  void 지원하지_않는_ContentType이면_예외가_발생한다() {
    assertThatThrownBy(() -> ImageUtils.extractExtension("video/mp4"))
        .isInstanceOf(ApplicationException.class)
        .satisfies(ex -> {
          ApplicationException appEx = (ApplicationException) ex;
          assertThat(appEx.getError()).isEqualTo(ApplicationError.UN_SUPPORTED_TYPE);
        });
  }

  @Test
  @DisplayName("이미지 파일 이름 생성 테스트")
  void 이미지_파일명을_생성할_수_있다() {
    String fileName = ImageUtils.generateImageFileName(PROFILE, "image/png");

    assertThat(fileName).startsWith("profile/");
    assertThat(fileName).endsWith(".png");
    assertThat(fileName.length()).isGreaterThan("profile/.png".length());
  }
}
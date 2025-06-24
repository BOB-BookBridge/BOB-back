package com.bob.global.utils;

import static com.bob.global.utils.image.ImageDirectory.PROFILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import com.bob.global.utils.image.ImageUtils;
import java.util.List;
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

  @Test
  @DisplayName("여러 개의 이미지 파일명을 생성할 수 있다")
  void 여러개의_이미지_파일명을_생성할_수_있다() {
    List<String> contentTypes = List.of("image/jpeg", "image/png", "image/gif");

    List<String> fileNames = ImageUtils.generateImageFileNames(PROFILE, contentTypes);

    assertThat(fileNames).hasSize(3);
    assertThat(fileNames.get(0)).startsWith("profile/");
    assertThat(fileNames.get(0)).endsWith(".jpg");
    assertThat(fileNames.get(1)).endsWith(".png");
    assertThat(fileNames.get(2)).endsWith(".gif");
  }
}
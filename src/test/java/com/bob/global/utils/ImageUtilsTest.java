package com.bob.global.utils;

import static com.bob.global.utils.image.ImageDirectory.PROFILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.global.utils.image.ImageUtils;

@DisplayName("이미지 Util 테스트")
class ImageUtilsTest {

    @Test
    void 확장자_추출() {
        assertThat(ImageUtils.extractExtension("image/jpeg")).isEqualTo("jpg");
        assertThat(ImageUtils.extractExtension("image/png")).isEqualTo("png");
        assertThat(ImageUtils.extractExtension("image/gif")).isEqualTo("gif");
    }

    @Test
    void 확장자_추출_시_지원하지_않는_타입이면_예외가_발생한다() {
        assertThatThrownBy(() -> ImageUtils.extractExtension("video/mp4"))
            .isInstanceOf(IllegalArgumentException.class)
            .satisfies(ex -> {
                assertThat(ex.getMessage()).contains("지원하지 않는 파일 형식입니다.");
            });
    }

    @Test
    void 파일명_생성() {
        String fileName = ImageUtils.generateImageFileName(PROFILE, "image/png");

        assertThat(fileName).startsWith("profile/");
        assertThat(fileName).endsWith(".png");
        assertThat(fileName.length()).isGreaterThan("profile/.png".length());
    }

    @Test
    void 파일명_목록_생성() {
        List<String> contentTypes = List.of("image/jpeg", "image/png", "image/gif");

        List<String> fileNames = ImageUtils.generateImageFileNames(PROFILE, contentTypes);

        assertThat(fileNames).hasSize(3);
        assertThat(fileNames.get(0)).startsWith("profile/");
        assertThat(fileNames.get(0)).endsWith(".jpg");
        assertThat(fileNames.get(1)).endsWith(".png");
        assertThat(fileNames.get(2)).endsWith(".gif");
    }
}

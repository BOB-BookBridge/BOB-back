package com.bob.global.utils.image;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("이미지 디렉토리 유틸 테스트")
class ImageDirectoryTest {

    @Test
    void ImageDirectory_조회() {
        assertThat(ImageDirectory.of("profile")).isEqualTo(ImageDirectory.PROFILE);
        assertThat(ImageDirectory.of("post")).isEqualTo(ImageDirectory.POST);
        assertThat(ImageDirectory.of("chat")).isEqualTo(ImageDirectory.CHAT);
    }
}

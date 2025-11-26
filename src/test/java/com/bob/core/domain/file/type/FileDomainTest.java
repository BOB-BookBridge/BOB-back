package com.bob.core.domain.file.type;

import static com.bob.global.exception.response.ApplicationError.UN_SUPPORTED_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.global.exception.exceptions.ApplicationException;

@DisplayName("파일 도메인 Enum 테스트")
class FileDomainTest {

    @Test
    void 대소문자_혼용() {
        FileDomain postDomain = FileDomain.of("PosT");
        FileDomain chatDomain = FileDomain.of("ChAt");

        assertThat(postDomain).isEqualTo(FileDomain.POST);
        assertThat(chatDomain).isEqualTo(FileDomain.CHAT);
    }

    @Test
    void 존재하지_않는_도메인이면_사용자_예외가_발생한다() {
        assertThatThrownBy(() -> FileDomain.of("invalid"))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(UN_SUPPORTED_TYPE.getMessage());
    }

    @Test
    void 빈_문자열_변환_시_사용자_예외가_발생한다() {
        assertThatThrownBy(() -> FileDomain.of(""))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(UN_SUPPORTED_TYPE.getMessage());
    }
}

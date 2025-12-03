package com.bob.core.domain.file.type;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("파일 도메인 Enum 테스트")
class FileDomainTest {

    @Test
    void 대소문자_혼용() {
        FileDomain postDomain = FileDomain.of("PosT");
        FileDomain chatDomain = FileDomain.of("ChAt");

        assertThat(postDomain).isEqualTo(FileDomain.POST);
        assertThat(chatDomain).isEqualTo(FileDomain.CHAT);
    }
}

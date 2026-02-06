package com.bob.admin.filter.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.admin.filter.application.dto.command.CreateFilterWordCommand;
import com.bob.admin.filter.domain.ManagementFilterWord;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import com.bob.infrastructure.data.filter.repository.FilterWordRepository;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.filter.word.FilterWordFixture;

@DisplayName("금칙어 생성 테스트")
@ContainerTest
record ManagementFilterWordRegisterTest(
    ManagementFilterWordRegister managementFilterWordRegister,
    FilterWordRepository wordRepository
) {

    @Test
    void 금칙어_등록() {
        var command = new CreateFilterWordCommand("욕");

        ManagementFilterWord result = managementFilterWordRegister.register(command);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getWord()).isEqualTo("욕");
        assertThat(result.isPredefined()).isFalse();
    }

    @Test
    void 금칙어_등록_시_중복_단어를_등록하면_사용자_예외가_발생한다() {
        wordRepository.save(FilterWordFixture.create("욕"));

        var command = new CreateFilterWordCommand("욕");

        assertThatThrownBy(() -> managementFilterWordRegister.register(command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ApplicationError.FILTER_WORD_ALREADY_EXISTS.getMessage());
    }
}

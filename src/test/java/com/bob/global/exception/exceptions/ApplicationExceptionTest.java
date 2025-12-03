package com.bob.global.exception.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.global.exception.response.ApplicationError;

@DisplayName("애플리케이션 예외 테스트")
class ApplicationExceptionTest {

    @Test
    void 원문_메시지_사용() {
        ApplicationError error = ApplicationError.NO_CHANGES;

        ApplicationException ex = new ApplicationException(error);

        assertThat(ex.getError()).isEqualTo(error);
        assertThat(ex.getArgs()).isEmpty();
        assertThat(ex.getMessage()).isEqualTo(error.getMessage());
    }

    @Test
    void 포맷_인수로_메시지_치환() {
        ApplicationError error = ApplicationError.BOOKCASE_ITEM_ALREADY_USE;
        Long usageId = 3L;
        String title = "제목";
        String expected = String.format(error.getMessage(), usageId, title);

        ApplicationException ex = new ApplicationException(error, usageId, title);

        assertThat(ex.getError()).isEqualTo(error);
        assertThat(ex.getArgs()).containsExactly(usageId, title);
        assertThat(ex.getMessage()).isEqualTo(expected);
    }

    @Test
    void 포맷_인수가_null이면_원문_메시지_사용() {
        ApplicationError error = ApplicationError.BOOKCASE_ITEM_ALREADY_USE;

        ApplicationException ex = new ApplicationException(error, (Object[])null);

        assertThat(ex.getError()).isEqualTo(error);
        assertThat(ex.getArgs()).isNull();
        assertThat(ex.getMessage()).isEqualTo(error.getMessage());
    }
}

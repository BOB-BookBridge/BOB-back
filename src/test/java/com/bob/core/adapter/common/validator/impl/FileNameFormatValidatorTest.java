package com.bob.core.adapter.common.validator.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import jakarta.validation.ConstraintValidatorContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("파일 이름 형식 검증 테스트")
@ExtendWith(MockitoExtension.class)
class FileNameFormatValidatorTest {

    private FileNameFormatValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        validator = new FileNameFormatValidator();
    }

    @Test
    void 유효한_파일_이름() {
        List<String> fileNames = List.of("post/abc123.jpg", "member/def456.png");

        boolean result = validator.isValid(fileNames, context);

        assertThat(result).isTrue();
    }

    @Test
    void 슬래시가_없는_파일_이름() {
        when(context.buildConstraintViolationWithTemplate("파일 이름은 'prefix/uuid.ext' 형식이어야 합니다."))
            .thenReturn(violationBuilder);

        List<String> fileNames = List.of("invalidfile.jpg");

        boolean result = validator.isValid(fileNames, context);

        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("파일 이름은 'prefix/uuid.ext' 형식이어야 합니다.");
        verify(violationBuilder).addConstraintViolation();
    }

    @Test
    void 확장자가_없는_파일_이름() {
        when(context.buildConstraintViolationWithTemplate("파일 이름은 'prefix/uuid.ext' 형식이어야 합니다."))
            .thenReturn(violationBuilder);

        List<String> fileNames = List.of("post/abc123");

        boolean result = validator.isValid(fileNames, context);

        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("파일 이름은 'prefix/uuid.ext' 형식이어야 합니다.");
        verify(violationBuilder).addConstraintViolation();
    }

    @Test
    void 확장자가_슬래시_앞에_있는_파일_이름() {
        when(context.buildConstraintViolationWithTemplate("파일 이름은 'prefix/uuid.ext' 형식이어야 합니다."))
            .thenReturn(violationBuilder);

        List<String> fileNames = List.of("post.jpg/abc123");

        boolean result = validator.isValid(fileNames, context);

        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("파일 이름은 'prefix/uuid.ext' 형식이어야 합니다.");
        verify(violationBuilder).addConstraintViolation();
    }

    @Test
    void 여러_파일_중_하나라도_유효하지_않으면_실패() {
        when(context.buildConstraintViolationWithTemplate("파일 이름은 'prefix/uuid.ext' 형식이어야 합니다."))
            .thenReturn(violationBuilder);

        List<String> fileNames = List.of("post/valid.jpg", "invalidfile");

        boolean result = validator.isValid(fileNames, context);

        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("파일 이름은 'prefix/uuid.ext' 형식이어야 합니다.");
        verify(violationBuilder).addConstraintViolation();
    }
}

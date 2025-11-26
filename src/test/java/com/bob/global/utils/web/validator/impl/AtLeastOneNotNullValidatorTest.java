package com.bob.global.utils.web.validator.impl;

import static com.bob.global.exception.response.ApplicationError.NO_CHANGES;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AtLeastOneNotNullValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void 둘중_하나라도_값이_있으면_검증_성공() {
        // age=20 유효
        TestRecord dto1 = new TestRecord(null, "nick", 20);
        // fileName="alice" 유효
        TestRecord dto2 = new TestRecord("alice", "nick", null);

        assertThat(validator.validate(dto1)).isEmpty();
        assertThat(validator.validate(dto2)).isEmpty();
    }

    @Test
    void 필수_요소_누락_시_예외_발생() {
        // fileName=null, age=null = 둘 다 값 없음
        TestRecord dto = new TestRecord(null, "nickname", null);

        Set<ConstraintViolation<TestRecord>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> NO_CHANGES.getMessage().equals(v.getMessage()));
    }

    @Test
    void 공백_문자열_null_취급_예외_발생() {
        // fileName="   ", age=null = 둘 다 값 없음(공백은 없음으로 간주)
        TestRecord dto = new TestRecord("   ", "nickname", null);

        Set<ConstraintViolation<TestRecord>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> NO_CHANGES.getMessage().equals(v.getMessage()));
    }
}

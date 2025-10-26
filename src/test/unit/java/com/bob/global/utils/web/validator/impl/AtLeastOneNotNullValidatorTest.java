package com.bob.global.utils.web.validator.impl;

import static com.bob.global.exception.response.ApplicationError.NO_CHANGES;
import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
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
    // given: age=20 → VALID
    TestRecord dto1 = new TestRecord(null, "nick", 20);
    // given: name="alice" → VALID
    TestRecord dto2 = new TestRecord("alice", "nick", null);

    // when & then
    assertThat(validator.validate(dto1)).isEmpty();
    assertThat(validator.validate(dto2)).isEmpty();
  }

  @Test
  void 필수_요소_누락_시_예외_발생() {
    // given: name=null, age=null = 둘 다 값 없음
    TestRecord dto = new TestRecord(null, "nickname", null);

    // when
    Set<ConstraintViolation<TestRecord>> violations = validator.validate(dto);

    // then
    assertThat(violations).anyMatch(v -> NO_CHANGES.getMessage().equals(v.getMessage()));
  }

  @Test
  void 공백_문자열_null_취급_예외_발생() {
    // given: name="   ", age=null = 둘 다 값 없음(공백은 없음으로 간주)
    TestRecord dto = new TestRecord("   ", "nickname", null);

    // when
    Set<ConstraintViolation<TestRecord>> violations = validator.validate(dto);

    // then
    assertThat(violations).anyMatch(v -> NO_CHANGES.getMessage().equals(v.getMessage()));
  }
}
package com.bob.core.shared.web.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Documented
@NotBlank(message = "domain은 필수입니다.")
@Pattern(
    regexp = "(?i)CHAT|POST",
    message = "domain은 CHAT, POST만 허용됩니다."
)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface ValidDomain {

    String message() default "유효하지 않은 domain입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

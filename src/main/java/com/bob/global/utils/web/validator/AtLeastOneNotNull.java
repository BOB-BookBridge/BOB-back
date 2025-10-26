package com.bob.global.utils.web.validator;


import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.bob.global.utils.web.validator.impl.AtLeastOneNotNullValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = AtLeastOneNotNullValidator.class)
public @interface AtLeastOneNotNull {

  String[] anyOf();

  String message() default "변경 사항이 없습니다.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
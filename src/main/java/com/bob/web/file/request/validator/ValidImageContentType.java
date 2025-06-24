package com.bob.web.file.request.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@NotBlank(message = "Content Type은 필수입니다.")
@Pattern(
    regexp = "(?i)image/(jpeg|png|gif)",
    message = "이미지 형식의 Content Type만 허용됩니다. (jpeg, png, gif)"
)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface ValidImageContentType {

  String message() default "유효하지 않은 Content Type입니다.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
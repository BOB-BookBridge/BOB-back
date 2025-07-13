package com.bob.web.chat.request.validator.impl;

import com.bob.web.chat.request.validator.ValidFileNameFormat;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Optional;

public class FileNameFormatValidator implements ConstraintValidator<ValidFileNameFormat, List<String>> {

  @Override
  public boolean isValid(List<String> fileNames, ConstraintValidatorContext context) {
    Optional<String> invalidFile = fileNames.stream()
        .filter(name -> !isValidFileName(name))
        .findFirst();

    if (invalidFile.isPresent()) {
      context
          .disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate("파일 이름은 'prefix/uuid.ext' 형식이어야 합니다.")
          .addConstraintViolation();
      return false;
    }
    return true;
  }

  private boolean isValidFileName(String fileName) {
    int slashIndex = fileName.lastIndexOf('/');
    int dotIndex = fileName.lastIndexOf('.');
    return slashIndex != -1 && dotIndex != -1 && dotIndex > slashIndex;
  }
}

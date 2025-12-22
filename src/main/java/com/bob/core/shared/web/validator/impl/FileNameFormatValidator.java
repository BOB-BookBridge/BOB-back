package com.bob.core.shared.web.validator.impl;

import java.util.List;
import java.util.Optional;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.bob.core.shared.web.validator.ValidFileNameFormat;

public class FileNameFormatValidator implements ConstraintValidator<ValidFileNameFormat, List<String>> {

    @Override
    public boolean isValid(List<String> fileNames, ConstraintValidatorContext context) {
        if (fileNames == null) {
            return true;
        }

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

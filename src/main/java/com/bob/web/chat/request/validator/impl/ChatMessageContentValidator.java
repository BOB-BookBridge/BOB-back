package com.bob.web.chat.request.validator.impl;

import com.bob.web.chat.request.CreateChatMessageRequest;
import com.bob.web.chat.request.validator.ValidChatMessageContent;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ChatMessageContentValidator implements ConstraintValidator<ValidChatMessageContent, CreateChatMessageRequest> {

  @Override
  public boolean isValid(CreateChatMessageRequest request, ConstraintValidatorContext context) {
    boolean messageExists = request.message() != null && !request.message().isBlank();
    boolean hasImages = request.fileNames() != null && !request.fileNames().isEmpty();
    return messageExists || hasImages;
  }
}

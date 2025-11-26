package com.bob.core.adapter.chat.api.request.validator.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.bob.core.adapter.chat.api.request.CreateChatMessageRequest;
import com.bob.core.adapter.chat.api.request.validator.ValidChatMessageContent;

public class ChatMessageContentValidator
    implements ConstraintValidator<ValidChatMessageContent, CreateChatMessageRequest> {

    @Override
    public boolean isValid(CreateChatMessageRequest request, ConstraintValidatorContext context) {
        boolean messageExists = request.message() != null && !request.message().isBlank();
        boolean hasImages = request.fileNames() != null && !request.fileNames().isEmpty();
        return messageExists || hasImages;
    }
}

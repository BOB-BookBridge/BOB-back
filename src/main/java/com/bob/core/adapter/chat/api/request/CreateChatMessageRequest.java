package com.bob.core.adapter.chat.api.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.bob.core.adapter.chat.api.request.validator.NotBlankIfNotNull;
import com.bob.core.adapter.chat.api.request.validator.ValidChatMessageContent;
import com.bob.core.adapter.common.validator.ValidFileNameFormat;

@ValidChatMessageContent
public record CreateChatMessageRequest(
    @NotBlankIfNotNull
    @Size(max = 500, message = "메시지는 최소 1자, 최대 500자까지 입력할 수 있습니다.")
    String message,

    @Size(max = 5, message = "사진은 최대 5개까지 전송이 가능합니다.")
    @ValidFileNameFormat
    List<@NotBlank(message = "사진 이름은 공백일 수 없습니다.") String> fileNames
) {

}

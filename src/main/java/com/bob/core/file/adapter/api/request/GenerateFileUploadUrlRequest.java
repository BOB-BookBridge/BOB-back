package com.bob.core.file.adapter.api.request;

import java.util.List;

import jakarta.validation.constraints.Size;

import lombok.Builder;

import com.bob.shared.web.annotation.AllowedValues;

@Builder
public record GenerateFileUploadUrlRequest(
    @AllowedValues(value = {"CHAT", "POST"}, ignoreCase = true)
    String domain,

    @Size(max = 5, message = "파일은 최대 5개까지 등록할 수 있습니다.")
    List<@AllowedValues(value = {"image/jpeg", "image/png", "image/gif"}, ignoreCase = true) String> contentTypes
) {

}

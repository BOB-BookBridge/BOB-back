package com.bob.core.file.adapter.api.request;

import java.util.List;

import jakarta.validation.constraints.Size;

import lombok.Builder;

import com.bob.core.shared.web.validator.ValidDomain;
import com.bob.core.shared.web.validator.ValidImageContentType;

@Builder
public record GenerateFileUploadUrlRequest(
    @ValidDomain
    String domain,

    @Size(max = 5, message = "파일은 최대 5개까지 등록할 수 있습니다.")
    List<@ValidImageContentType String> contentTypes
) {

}

package com.bob.domain.file.service.dto.response.internal;

public record PreSignedUrlResponse(
    int sequence,
    String fileName,
    String fileUploadUrl
) {

}

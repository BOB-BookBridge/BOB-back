package com.bob.domain.file.service.dto.command;

import java.util.List;
import lombok.Builder;

@Builder
public record ReadMultiFileUploadUrlQuery(
    String domain,
    List<String> contentTypes
) {

}
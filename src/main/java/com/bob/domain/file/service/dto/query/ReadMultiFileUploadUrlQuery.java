package com.bob.domain.file.service.dto.query;

import java.util.List;
import lombok.Builder;

@Builder
public record ReadMultiFileUploadUrlQuery(
    String domain,
    List<String> contentTypes
) {

}

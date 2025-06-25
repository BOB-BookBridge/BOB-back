package com.bob.domain.file.service.dto.query;

import java.util.List;
import lombok.Builder;

@Builder
public record ReadFileUploadUrlQuery(
    String domain,
    List<String> contentTypes
) {

}

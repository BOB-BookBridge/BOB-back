package com.bob.domain.file.service.dto.response;

import com.bob.domain.file.service.dto.response.internal.FileSummaryResponse;
import java.util.List;

public record FilesResponse(
    List<FileSummaryResponse> summaries
) {

}

package com.bob.web.file.request;

import com.bob.domain.file.service.dto.query.ReadSingleFileUploadUrlQuery;
import com.bob.web.file.request.validator.ValidDomain;
import com.bob.web.file.request.validator.ValidImageContentType;

public record ReadSingleFileUploadUrlRequest(
    @ValidDomain
    String domain,

    @ValidImageContentType
    String contentType
) {

  public ReadSingleFileUploadUrlQuery toQuery() {
    return new ReadSingleFileUploadUrlQuery(domain, contentType);
  }
}

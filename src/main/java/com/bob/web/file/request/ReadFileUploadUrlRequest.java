package com.bob.web.file.request;

import com.bob.domain.file.service.dto.query.ReadFileUploadUrlQuery;
import com.bob.web.file.request.validator.ValidDomain;
import com.bob.web.file.request.validator.ValidImageContentType;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;

@Builder
public record ReadFileUploadUrlRequest(
    @ValidDomain
    String domain,

    @Size(max = 5, message = "이미지는 최대 5개까지 등록할 수 있습니다.")
    List<@ValidImageContentType String> contentTypes
) {

  public ReadFileUploadUrlQuery toQuery() {
    return ReadFileUploadUrlQuery.builder()
        .domain(domain)
        .contentTypes(contentTypes)
        .build();
  }
}

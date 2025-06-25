package com.bob.web.file.request;

import com.bob.domain.file.service.dto.command.ChangeFileCommand;
import com.bob.web.file.request.validator.ValidDomain;
import com.bob.web.file.request.validator.ValidFileNameFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ChangeFileRequest(
    @ValidDomain
    String domain,

    @NotBlank(message = "참조 ID는 필수입니다.")
    String referenceId,

    @NotNull(message = "파일 이름 목록은 필수입니다.")
    @Size(min = 1, max = 5, message = "파일은 1개 이상 5개 이하만 등록 가능합니다.")
    @ValidFileNameFormat
    List<@NotBlank(message = "파일 이름은 비어 있을 수 없습니다.") String> fileNames
) {

  public ChangeFileCommand toCommand(UUID memberId) {
    return ChangeFileCommand.builder()
        .domain(domain)
        .referenceId(referenceId)
        .fileNames(fileNames)
        .memberId(memberId)
        .build();
  }
}

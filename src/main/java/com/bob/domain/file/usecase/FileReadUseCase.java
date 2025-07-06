package com.bob.domain.file.usecase;

import com.bob.domain.file.service.dto.query.ReadFilesWithDomainIdQuery;
import com.bob.domain.file.service.dto.response.FilesResponse;

public interface FileReadUseCase {

  FilesResponse readFilesByDomainId(ReadFilesWithDomainIdQuery query);
}

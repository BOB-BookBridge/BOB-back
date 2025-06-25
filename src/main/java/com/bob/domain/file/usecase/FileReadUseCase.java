package com.bob.domain.file.usecase;

import com.bob.domain.file.service.dto.query.ReadFileUploadUrlQuery;
import com.bob.domain.file.service.dto.query.ReadFilesWithDomainIdQuery;
import com.bob.domain.file.service.dto.response.FileUploadUrlResponse;
import com.bob.domain.file.service.dto.response.FilesResponse;

public interface FileReadUseCase {

  FileUploadUrlResponse readFileUploadUrl(ReadFileUploadUrlQuery query);

  FilesResponse readFilesByDomainId(ReadFilesWithDomainIdQuery query);
}

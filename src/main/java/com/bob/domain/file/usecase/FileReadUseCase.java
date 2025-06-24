package com.bob.domain.file.usecase;

import com.bob.domain.file.service.dto.command.ReadMultiFileUploadUrlQuery;
import com.bob.domain.file.service.dto.query.ReadSingleFileUploadUrlQuery;
import com.bob.domain.file.service.dto.response.ReadMultiFileUploadUrlResponse;
import com.bob.domain.file.service.dto.response.ReadSingleFileUploadUrlResponse;

public interface FileReadUseCase {

  ReadSingleFileUploadUrlResponse readSingleFileUploadUrl(ReadSingleFileUploadUrlQuery query);

  ReadMultiFileUploadUrlResponse readMultiFileUploadUrl(ReadMultiFileUploadUrlQuery query);
}

package com.bob.domain.file.service;

import static com.bob.global.utils.image.ImageDirectory.from;
import static com.bob.global.utils.image.ImageUtils.generateImageFileName;
import static com.bob.global.utils.image.ImageUtils.generateImageFileNames;

import com.bob.domain.file.service.dto.command.ReadMultiFileUploadUrlQuery;
import com.bob.domain.file.service.dto.query.ReadSingleFileUploadUrlQuery;
import com.bob.domain.file.service.dto.response.ReadMultiFileUploadUrlResponse;
import com.bob.domain.file.service.dto.response.ReadSingleFileUploadUrlResponse;
import com.bob.domain.file.service.port.FileImagePort;
import com.bob.domain.file.usecase.FileReadUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FileService implements FileReadUseCase {

  private final FileImagePort imagePort;

  public ReadSingleFileUploadUrlResponse readSingleFileUploadUrl(ReadSingleFileUploadUrlQuery query) {
    String fileName = generateImageFileName(from(query.domain()), query.contentType());
    String preSignedUrl = imagePort.generateSingleFileUploadUrlProcess(fileName, query.contentType());
    return ReadSingleFileUploadUrlResponse.from(fileName, preSignedUrl);
  }

  public ReadMultiFileUploadUrlResponse readMultiFileUploadUrl(ReadMultiFileUploadUrlQuery query) {
    List<String> fileNames = generateImageFileNames(from(query.domain()), query.contentTypes());
    List<String> preSignedUrls = imagePort.generateMultiFileUploadUrlsProcess(fileNames, query.contentTypes());
    return ReadMultiFileUploadUrlResponse.from(fileNames, preSignedUrls);
  }
}

package com.bob.domain.file.service;

import static com.bob.global.utils.image.ImageDirectory.from;
import static com.bob.global.utils.image.ImageUtils.generateImageFileName;
import static com.bob.global.utils.image.ImageUtils.generateImageFileNames;

import com.bob.domain.file.entity.File;
import com.bob.domain.file.repository.FileRepository;
import com.bob.domain.file.service.dto.command.ModifyReferenceIdCommand;
import com.bob.domain.file.service.dto.command.RegisterFileCommand;
import com.bob.domain.file.service.dto.query.ReadFilesWithDomainIdQuery;
import com.bob.domain.file.service.dto.query.ReadMultiFileUploadUrlQuery;
import com.bob.domain.file.service.dto.query.ReadSingleFileUploadUrlQuery;
import com.bob.domain.file.service.dto.response.ReadFilesResponse;
import com.bob.domain.file.service.dto.response.ReadMultiFileUploadUrlResponse;
import com.bob.domain.file.service.dto.response.ReadSingleFileUploadUrlResponse;
import com.bob.domain.file.service.dto.response.internal.FileSummaryResponse;
import com.bob.domain.file.service.port.FileImagePort;
import com.bob.domain.file.service.reader.FileReader;
import com.bob.domain.file.usecase.FileModifyUseCase;
import com.bob.domain.file.usecase.FileReadUseCase;
import com.bob.domain.file.usecase.FileWriteUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FileService implements FileWriteUseCase, FileReadUseCase, FileModifyUseCase {

  private final FileRepository fileRepository;
  private final FileReader fileReader;

  private final FileImagePort imagePort;

  @Transactional
  public void registerFileProcess(RegisterFileCommand command) {
    fileRepository.saveAll(command.toEntities());
  }

  @Transactional
  public void modifyReferenceIdProcess(ModifyReferenceIdCommand command) {
    List<File> files = fileReader.readFileByReferenceId(command.oldReferenceId());
    files.forEach(file -> file.updateReferenceId(String.valueOf(command.currentReferenceId())));
  }

  @Transactional(readOnly = true)
  public ReadFilesResponse readFilesByDomainId(ReadFilesWithDomainIdQuery query) {
    List<File> files = fileReader.readFileByReferenceId(query.domainId());
    return new ReadFilesResponse(FileSummaryResponse.from(files));
  }

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

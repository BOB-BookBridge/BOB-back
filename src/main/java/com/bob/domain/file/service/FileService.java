package com.bob.domain.file.service;

import static com.bob.global.utils.image.ImageDirectory.from;
import static com.bob.global.utils.image.ImageUtils.generateImageFileNames;

import com.bob.domain.file.entity.File;
import com.bob.domain.file.repository.FileRepository;
import com.bob.domain.file.service.dto.command.ModifyReferenceIdCommand;
import com.bob.domain.file.service.dto.command.RegisterFileCommand;
import com.bob.domain.file.service.dto.query.ReadFileUploadUrlQuery;
import com.bob.domain.file.service.dto.query.ReadFilesWithDomainIdQuery;
import com.bob.domain.file.service.dto.response.FileUploadUrlResponse;
import com.bob.domain.file.service.dto.response.FilesResponse;
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

  @Transactional(readOnly = true)
  public FilesResponse readFilesByDomainId(ReadFilesWithDomainIdQuery query) {
    List<File> files = fileReader.readFileByReferenceId(query.domainId());
    return new FilesResponse(FileSummaryResponse.from(files));
  }

  @Transactional
  public void modifyReferenceIdProcess(ModifyReferenceIdCommand command) {
    List<File> files = fileReader.readFileByReferenceId(command.oldReferenceId());
    files.forEach(file -> file.updateReferenceId(String.valueOf(command.currentReferenceId())));
  }

  public FileUploadUrlResponse readFileUploadUrl(ReadFileUploadUrlQuery query) {
    List<String> fileNames = generateImageFileNames(from(query.domain()), query.contentTypes());
    List<String> preSignedUrls = imagePort.generateFileUploadUrlsProcess(fileNames, query.contentTypes());
    return FileUploadUrlResponse.from(fileNames, preSignedUrls);
  }
}

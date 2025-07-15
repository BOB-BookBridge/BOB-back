package com.bob.domain.file.service.reader;

import com.bob.domain.file.entity.File;
import com.bob.domain.file.entity.type.FileDomain;
import com.bob.domain.file.repository.FileRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FileReader {

  private final FileRepository fileRepository;

  public List<File> readFileByReferenceId(FileDomain domain, String refId) {
    return fileRepository.findByDomainAndReferenceId(domain, refId);
  }

  public Optional<File> readFileByFileName(String fileName) {
    return fileRepository.findByFileName(fileName);
  }
}

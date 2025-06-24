package com.bob.domain.file.service.reader;

import com.bob.domain.file.entity.File;
import com.bob.domain.file.repository.FileRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FileReader {

  private final FileRepository fileRepository;

  public List<File> readFileByReferenceId(String referenceId) {
    return fileRepository.findByReferenceId(referenceId);
  }
}

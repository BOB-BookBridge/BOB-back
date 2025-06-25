package com.bob.domain.file.repository;

import com.bob.domain.file.entity.File;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface FileRepository extends CrudRepository<File, Long> {

  List<File> findByReferenceId(String referenceId);

  Optional<File> findByFileName(String fileName);
}

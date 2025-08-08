package com.bob.domain.file.repository;

import com.bob.domain.file.entity.File;
import com.bob.domain.file.entity.type.FileDomain;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface FileRepository extends CrudRepository<File, Long> {

  List<File> findByDomainAndReferenceId(FileDomain domain, String referenceId);

  List<File> findByReferenceIdIsNull();

  Optional<File> findByFileName(String fileName);
}

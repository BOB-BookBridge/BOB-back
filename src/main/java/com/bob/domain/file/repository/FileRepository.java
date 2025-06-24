package com.bob.domain.file.repository;

import com.bob.domain.file.entity.File;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface FileRepository extends CrudRepository<File, Long> {

  List<File> findByReferenceId(String referenceId);
}

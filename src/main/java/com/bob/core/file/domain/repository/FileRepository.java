package com.bob.core.file.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.bob.core.file.domain.File;
import com.bob.core.file.domain.type.FileDomain;

public interface FileRepository extends CrudRepository<File, Long> {

    List<File> findByDomainAndReferenceId(FileDomain domain, String referenceId);

    List<File> findByReferenceIdIsNull();

    Optional<File> findByName(String name);
}

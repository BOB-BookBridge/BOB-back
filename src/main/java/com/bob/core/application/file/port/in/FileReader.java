package com.bob.core.application.file.port.in;

import java.util.List;

import com.bob.core.application.file.dto.query.ReadFilesWithDomainIdQuery;
import com.bob.core.domain.file.File;

public interface FileReader {

    List<File> readUnusedFiles();

    List<File> readByDomainId(ReadFilesWithDomainIdQuery query);

    File readByName(String name);
}

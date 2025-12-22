package com.bob.core.file.application.port.in;

import java.util.List;

import com.bob.core.file.application.dto.query.ReadFilesWithDomainIdQuery;
import com.bob.core.file.domain.File;

public interface FileReader {

    List<File> readUnusedFiles();

    List<File> readByDomainId(ReadFilesWithDomainIdQuery query);

    File readByName(String name);
}

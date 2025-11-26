package com.bob.core.application.file;

import static com.bob.core.domain.file.type.FileDomain.of;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.application.file.dto.query.ReadFilesWithDomainIdQuery;
import com.bob.core.application.file.port.in.FileReader;
import com.bob.core.domain.file.File;
import com.bob.core.domain.file.repository.FileRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileQueryService implements FileReader {

    private final FileRepository fileRepository;

    @Override
    public List<File> readUnusedFiles() {
        return fileRepository.findByReferenceIdIsNull();
    }

    @Override
    public File readByName(String name) {
        return fileRepository.findByName(name)
            .orElseThrow(() -> new ApplicationException(ApplicationError.NOT_EXIST_OBJECT));
    }

    @Override
    public List<File> readByDomainId(ReadFilesWithDomainIdQuery query) {
        return fileRepository.findByDomainAndReferenceId(of(query.domain()), query.referenceId());
    }
}

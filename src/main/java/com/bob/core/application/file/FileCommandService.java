package com.bob.core.application.file;

import static com.bob.core.domain.file.File.createFile;
import static com.bob.core.domain.file.type.FileDomain.of;
import static com.bob.global.exception.response.ApplicationError.FILE_UNAUTHORIZED;
import static com.bob.global.utils.image.ImageDirectory.from;
import static com.bob.global.utils.image.ImageUtils.generateImageFileNames;
import static com.bob.global.utils.stream.StreamUtils.forEachWithIndex;
import static java.util.stream.IntStream.range;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.application.file.dto.command.GenerateFileUploadUrlCommand;
import com.bob.core.application.file.dto.command.MappingFileReferencesCommand;
import com.bob.core.application.file.dto.command.RegisterFilesCommand;
import com.bob.core.application.file.dto.command.UpdateFilesCommand;
import com.bob.core.application.file.dto.query.ReadFilesWithDomainIdQuery;
import com.bob.core.application.file.dto.result.FileUploadUrl;
import com.bob.core.application.file.port.in.FileModifier;
import com.bob.core.application.file.port.in.FileReader;
import com.bob.core.application.file.port.in.FileRegister;
import com.bob.core.application.file.port.in.FileRemover;
import com.bob.core.application.file.port.out.FileStoragePort;
import com.bob.core.domain.file.File;
import com.bob.core.domain.file.repository.FileRepository;
import com.bob.global.exception.exceptions.ApplicationException;

@Service
@Transactional
@RequiredArgsConstructor
public class FileCommandService implements FileRegister, FileModifier, FileRemover {

    private final FileRepository fileRepository;
    private final FileReader fileReader;

    private final FileStoragePort storagePort;

    @Override
    public List<FileUploadUrl> generateFileUploadUrl(GenerateFileUploadUrlCommand command) {
        List<String> fileNames = generateImageFileNames(from(command.domain()), command.contentTypes());

        List<String> uploadUrls = storagePort.generateUploadUrls(fileNames, command.contentTypes());

        return range(0, fileNames.size())
            .mapToObj(seq -> new FileUploadUrl(seq, fileNames.get(seq), uploadUrls.get(seq)))
            .toList();
    }

    @Override
    public List<File> registerFiles(RegisterFilesCommand command) {
        List<File> files = command.fileNames().stream()
            .map((n) -> createFile(of(command.domain()), n, null, command.memberId()))
            .toList();

        fileRepository.saveAll(files);

        return files;
    }

    @Override
    public List<File> updateFiles(UpdateFilesCommand command) {
        List<File> oldFiles = fileReader.readByDomainId(
            new ReadFilesWithDomainIdQuery(command.domain(), command.referenceId()));

        verifyOwner(oldFiles, command.memberId());

        fileRepository.deleteAll(oldFiles);

        List<File> files = range(0, command.names().size())
            .mapToObj(i -> {
                File file = createFile(of(command.domain()), command.names().get(i), i, command.memberId());
                file.mappingReferenceId(i, command.referenceId());
                return file;
            })
            .toList();

        fileRepository.saveAll(files);

        return files;
    }

    private void verifyOwner(List<File> existingFiles, UUID memberId) {
        if (existingFiles.stream().anyMatch(file -> !Objects.equals(file.getUploader(), memberId)))
            throw new ApplicationException(FILE_UNAUTHORIZED);
    }

    @Override
    public void mappingReferences(MappingFileReferencesCommand command) {
        forEachWithIndex(command.names(), (index, name) -> {
                try {
                    fileReader.readByName(name).mappingReferenceId(index, String.valueOf(command.referenceId()));
                } catch (ApplicationException ignored) {
                }
            }
        );
    }

    @Override
    public void removeUnusedFiles() {
        List<File> files = fileReader.readUnusedFiles();

        fileRepository.deleteAll(files);

        storagePort.deleteFiles(files.stream().map(File::getName).toList());
    }
}

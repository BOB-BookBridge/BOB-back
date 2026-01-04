package com.bob.infrastructure.storage.adapter;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.file.application.port.out.infra.FileStoragePort;
import com.bob.infrastructure.storage.repository.FileRepository;

@Component
@RequiredArgsConstructor
public class FileStorageAdapter implements FileStoragePort {

    private final FileRepository fileRepository;

    @Override
    public List<String> generateUploadUrls(List<String> fileNames, List<String> contentTypes) {
        return fileRepository.generateUploadUrls(fileNames, contentTypes);
    }

    @Override
    public void deleteFiles(List<String> fileNames) {
        fileRepository.deleteFiles(fileNames);
    }
}

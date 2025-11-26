package com.bob.infrastructure.storage.repository;

import java.util.List;

public interface FileRepository {

    List<String> generateUploadUrls(List<String> fileNames, List<String> contentTypes);

    String generateUploadUrl(String fileName, String contentType);

    void deleteFiles(List<String> fileNames);
}
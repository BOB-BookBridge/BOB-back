package com.bob.core.application.file.port.out;

import java.util.List;

public interface FileStoragePort {

    List<String> generateUploadUrls(List<String> fileNames, List<String> contentTypes);

    void deleteFiles(List<String> fileNames);
}

package com.bob.core.file.application.port.out.infra;

import java.util.List;

public interface FileStoragePort {

    List<String> generateUploadUrls(List<String> fileNames, List<String> contentTypes);

    void deleteFiles(List<String> fileNames);
}

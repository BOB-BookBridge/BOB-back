package com.bob.domain.file.service.port;

import java.util.List;

public interface FileS3Port {

  List<String> generateFileUploadUrlsProcess(List<String> fileNames, List<String> contentTypes);

  void removeUnusedFilesProcess(List<String> fileNames);
}

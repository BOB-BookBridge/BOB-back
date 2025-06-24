package com.bob.domain.file.service.port;

import java.util.List;

public interface FileImagePort {

  String generateSingleFileUploadUrlProcess(String fileName, String contentType);

  List<String> generateMultiFileUploadUrlsProcess(List<String> fileNames, List<String> contentTypes);
}

package com.bob.domain.file.service.port;

import java.util.List;

public interface FileImagePort {

  List<String> generateFileUploadUrlsProcess(List<String> fileNames, List<String> contentTypes);
}

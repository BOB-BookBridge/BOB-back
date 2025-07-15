package com.bob.domain.chat.service.port.out;

import com.bob.domain.file.service.dto.response.FilesResponse;
import java.util.List;

public interface ChatFilePort {

  FilesResponse readChatFileSummaries(Long chatRoomId);

  void modifyReferenceId(List<String> fileNames, String domainId);
}

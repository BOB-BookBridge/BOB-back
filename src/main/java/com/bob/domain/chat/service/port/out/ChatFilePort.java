package com.bob.domain.chat.service.port.out;

import java.util.List;

public interface ChatFilePort {

  void modifyReferenceId(List<String> fileNames, String domainId);
}

package com.bob.core.application.chat.port.out;

import java.util.List;

import com.bob.core.application.chat.port.result.ChatFile;

public interface ChatFilePort {

    List<ChatFile> read(Long chatRoomId);

    void mappingReferenceId(List<String> fileNames, String referenceId);
}

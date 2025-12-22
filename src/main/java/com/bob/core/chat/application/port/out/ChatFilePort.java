package com.bob.core.chat.application.port.out;

import java.util.List;

import com.bob.core.chat.application.port.result.ChatFile;

public interface ChatFilePort {

    List<ChatFile> read(Long chatRoomId);

    void mappingReferenceId(List<String> fileNames, String referenceId);
}

package com.bob.integration.adapter.chat;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.application.chat.port.out.ChatFilePort;
import com.bob.core.application.chat.port.result.ChatFile;
import com.bob.core.application.file.dto.command.MappingFileReferencesCommand;
import com.bob.core.application.file.dto.query.ReadFilesWithDomainIdQuery;
import com.bob.core.application.file.port.in.FileModifier;
import com.bob.core.application.file.port.in.FileReader;
import com.bob.core.domain.file.File;

@Component
@RequiredArgsConstructor
public class ChatFileAdapter implements ChatFilePort {

    private final FileReader readUseCase;
    private final FileModifier modifyUseCase;

    @Override
    public List<ChatFile> read(Long chatRoomId) {
        ReadFilesWithDomainIdQuery query = new ReadFilesWithDomainIdQuery("CHAT", chatRoomId.toString());

        List<File> files = readUseCase.readByDomainId(query);

        return files.stream()
            .map(file -> new ChatFile(file.getSequence(), file.getName()))
            .toList();
    }

    @Override
    public void mappingReferenceId(List<String> fileNames, String referenceId) {
        MappingFileReferencesCommand command = new MappingFileReferencesCommand(fileNames, referenceId);

        modifyUseCase.mappingReferences(command);
    }
}

package com.bob.integration.adapter.post;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.application.file.dto.command.MappingFileReferencesCommand;
import com.bob.core.application.file.dto.query.ReadFilesWithDomainIdQuery;
import com.bob.core.application.file.port.in.FileModifier;
import com.bob.core.application.file.port.in.FileReader;
import com.bob.core.application.post.port.out.PostFilePort;
import com.bob.core.application.post.port.result.PostFile;
import com.bob.core.domain.file.File;

@Component
@RequiredArgsConstructor
public class PostFileAdapter implements PostFilePort {

    private final FileReader fileReader;
    private final FileModifier fileModifier;

    @Override
    public List<PostFile> readPostFiles(Long postId) {
        ReadFilesWithDomainIdQuery query = new ReadFilesWithDomainIdQuery("POST", postId.toString());

        List<File> result = fileReader.readByDomainId(query);

        return result.stream()
            .map(file -> new PostFile(file.getSequence(), file.getName()))
            .toList();
    }

    @Override
    public void changeReferenceId(List<String> fileNames, String referenceId) {
        MappingFileReferencesCommand command = new MappingFileReferencesCommand(fileNames, referenceId);

        fileModifier.mappingReferences(command);
    }
}

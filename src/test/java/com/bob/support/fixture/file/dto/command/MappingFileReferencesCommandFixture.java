package com.bob.support.fixture.file.dto.command;

import java.util.List;

import com.bob.core.application.file.dto.command.MappingFileReferencesCommand;

public class MappingFileReferencesCommandFixture {

    public static MappingFileReferencesCommand createMappingFileReferencesCommand(
        List<String> names, String referenceId
    ) {
        return new MappingFileReferencesCommand(names, referenceId);
    }

    public static MappingFileReferencesCommand createMappingFileReferencesCommand(String referenceId) {
        return createMappingFileReferencesCommand(List.of("post/1.jpg", "post/2.jpg", "post/3.jpg"), referenceId);
    }
}

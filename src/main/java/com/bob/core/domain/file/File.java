package com.bob.core.domain.file;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.bob.core.domain.AbstractEntity;
import com.bob.core.domain.file.type.FileDomain;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class File extends AbstractEntity {

    private FileDomain domain;

    private String referenceId;

    private String name;

    private Integer sequence;

    private UUID uploader;

    private LocalDateTime createdAt;

    public static File createFile(FileDomain domain, String name, Integer sequence, UUID uploader) {
        return File.builder()
            .domain(domain)
            .referenceId(null)
            .name(name)
            .sequence(sequence)
            .uploader(uploader)
            .createdAt(LocalDateTime.now())
            .build();
    }

    public void mappingReferenceId(int sequence, String referenceId) {
        this.sequence = sequence;
        this.referenceId = referenceId;
    }
}

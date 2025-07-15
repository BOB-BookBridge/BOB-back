package com.bob.domain.file.service.dto.query;

import com.bob.domain.file.entity.type.FileDomain;

public record ReadFilesWithDomainIdQuery(
    FileDomain domain,
    String domainId
) {

}

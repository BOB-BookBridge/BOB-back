package com.bob.admin.post.application.port.result;

import java.util.List;

public record ManagementPostSummaries(Long totalCount, List<ManagementPost> posts) {

}

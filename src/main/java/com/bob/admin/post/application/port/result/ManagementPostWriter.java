package com.bob.admin.post.application.port.result;

import java.util.UUID;

public record ManagementPostWriter(UUID id, String email, String nickname) {

}

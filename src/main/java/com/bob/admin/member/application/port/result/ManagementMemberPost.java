package com.bob.admin.member.application.port.result;

import java.util.List;

public record ManagementMemberPost(Integer count, List<Long> written) {

}

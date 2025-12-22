package com.bob.admin.application.port.result.activity;

import java.util.List;

public record ManagementMemberTrade(Long count, List<Long> sold, List<Long> bought) {

}

package com.bob.core.application.management.port.result;

import com.bob.core.application.management.port.result.activity.ManagementMemberPost;
import com.bob.core.application.management.port.result.activity.ManagementMemberTrade;

public record ManagementMemberActivity(ManagementMemberPost post, ManagementMemberTrade trade) {

}

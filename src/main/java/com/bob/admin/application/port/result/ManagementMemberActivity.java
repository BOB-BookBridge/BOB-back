package com.bob.admin.application.port.result;

import com.bob.admin.application.port.result.activity.ManagementMemberPost;
import com.bob.admin.application.port.result.activity.ManagementMemberTrade;

public record ManagementMemberActivity(ManagementMemberPost post, ManagementMemberTrade trade) {

}

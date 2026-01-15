package com.bob.admin.report.application.port.out;

import java.util.UUID;

public interface ManagementReportMemberPort {

    void ban(UUID reportedId, String memo);

    void updateMemo(UUID reportedId, String memo);
}

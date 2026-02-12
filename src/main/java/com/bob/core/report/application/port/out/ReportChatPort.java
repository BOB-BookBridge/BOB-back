package com.bob.core.report.application.port.out;

import java.util.List;

public interface ReportChatPort {

    List<Long> readMessageIds(Long messageId);
}

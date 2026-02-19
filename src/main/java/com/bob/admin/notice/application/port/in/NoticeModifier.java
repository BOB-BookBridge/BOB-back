package com.bob.admin.notice.application.port.in;

import com.bob.admin.notice.domain.Notice;

public interface NoticeModifier {

    Notice deactivateCurrentBanner();
}

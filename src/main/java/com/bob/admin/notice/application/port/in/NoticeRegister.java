package com.bob.admin.notice.application.port.in;

import com.bob.admin.notice.application.dto.command.RegisterAlertCommand;
import com.bob.admin.notice.application.dto.command.RegisterBannerCommand;
import com.bob.admin.notice.domain.Notice;

public interface NoticeRegister {

    Notice registerBanner(RegisterBannerCommand command);

    Notice registerAlert(RegisterAlertCommand command);
}

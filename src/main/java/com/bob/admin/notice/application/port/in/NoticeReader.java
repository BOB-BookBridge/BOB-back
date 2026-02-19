package com.bob.admin.notice.application.port.in;

import java.util.List;
import java.util.Optional;

import com.bob.admin.notice.application.port.result.AlertNotice;
import com.bob.admin.notice.application.port.result.BannerNotice;
import com.bob.admin.notice.application.port.result.NoticeDetail;
import com.bob.admin.notice.domain.Notice;

public interface NoticeReader {

    Notice read(Long noticeId);

    NoticeDetail readDetail(Long noticeId);

    Optional<BannerNotice> readBanner();

    List<AlertNotice> readCurrentAlertNotices();
}

package com.bob.admin.notice.application.port.in;

import java.util.Optional;

import com.bob.admin.notice.application.port.result.BannerNotice;

public interface NoticeReader {

    Optional<BannerNotice> readBanner();
}

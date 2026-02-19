package com.bob.admin.notice.application;

import java.time.LocalDateTime;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.admin.notice.application.port.in.NoticeReader;
import com.bob.admin.notice.application.port.out.NoticeMemberPort;
import com.bob.admin.notice.application.port.result.BannerNotice;
import com.bob.admin.notice.domain.repository.NoticeRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeQueryService implements NoticeReader {

    private final NoticeRepository noticeRepository;

    private final NoticeMemberPort noticeMemberPort;

    @Override
    public Optional<BannerNotice> readBanner() {
        return noticeRepository.findCurrentBanner(LocalDateTime.now(), PageRequest.of(0, 1)).stream()
            .findFirst()
            .map(notice -> new BannerNotice(
                notice.getTitle(),
                notice.getContent(),
                notice.getEndsAt(),
                notice.getWriterId(),
                noticeMemberPort.readNickname(notice.getWriterId())
            ));
    }
}

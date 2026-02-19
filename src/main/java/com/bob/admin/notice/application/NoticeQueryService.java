package com.bob.admin.notice.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.admin.notice.application.port.in.NoticeReader;
import com.bob.admin.notice.application.port.out.NoticeMemberPort;
import com.bob.admin.notice.application.port.result.AlertNotice;
import com.bob.admin.notice.application.port.result.BannerNotice;
import com.bob.admin.notice.application.port.result.NoticeDetail;
import com.bob.admin.notice.domain.Notice;
import com.bob.admin.notice.domain.NoticeType;
import com.bob.admin.notice.domain.repository.NoticeRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeQueryService implements NoticeReader {

    private final NoticeRepository noticeRepository;

    private final NoticeMemberPort noticeMemberPort;

    @Override
    public Notice read(Long noticeId) {
        return noticeRepository.findById(noticeId)
            .orElseThrow(() -> new IllegalArgumentException("공지를 찾을 수 없습니다. id: " + noticeId));
    }

    @Override
    public NoticeDetail readDetail(Long noticeId) {
        Notice notice = read(noticeId);

        String writerNickname = noticeMemberPort.readNickname(notice.getWriterId());

        return new NoticeDetail(notice.getWriterId(), writerNickname, notice.getTitle(), notice.getContent(),
            notice.getCreatedAt());
    }

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

    @Override
    public List<AlertNotice> readCurrentAlertNotices() {
        return noticeRepository.findByTypeOrderByCreatedAtDesc(NoticeType.ALERT, PageRequest.of(0, 5)).stream()
            .map(notice -> new AlertNotice(
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedAt(),
                notice.getWriterId(),
                noticeMemberPort.readNickname(notice.getWriterId())
            )).toList();
    }
}

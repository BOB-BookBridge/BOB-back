package com.bob.admin.notice.application;

import static com.bob.admin.notice.domain.Notice.createAlert;
import static com.bob.admin.notice.domain.Notice.createBanner;
import static java.time.LocalDateTime.now;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.admin.notice.application.dto.command.RegisterAlertCommand;
import com.bob.admin.notice.application.dto.command.RegisterBannerCommand;
import com.bob.admin.notice.application.port.in.NoticeModifier;
import com.bob.admin.notice.application.port.in.NoticeRegister;
import com.bob.admin.notice.domain.Notice;
import com.bob.admin.notice.domain.repository.NoticeRepository;
import com.bob.shared.event.NoticeNotificationEvent;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeCommandService implements NoticeRegister, NoticeModifier {

    private final NoticeRepository noticeRepository;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Notice registerBanner(RegisterBannerCommand command) {
        noticeRepository.findCurrentBanner(now(), PageRequest.of(0, 1)).stream()
            .findFirst()
            .ifPresent(Notice::deactivate);

        Notice notice = createBanner(command.writerId(), command.content(), command.endTime());

        return noticeRepository.save(notice);
    }

    @Override
    public Notice registerAlert(RegisterAlertCommand command) {
        Notice notice = noticeRepository.save(createAlert(command.writerId(), command.title(), command.content()));

        NoticeNotificationEvent event = new NoticeNotificationEvent(notice.getId(), notice.getWriterId());

        eventPublisher.publishEvent(event);

        return notice;
    }

    @Override
    public Notice deactivateCurrentBanner() {
        return noticeRepository.findCurrentBanner(now(), PageRequest.of(0, 1)).stream()
            .findFirst()
            .map(notice -> {
                notice.deactivate();
                return notice;
            })
            .orElseThrow(() -> new IllegalStateException("활성화 되어있는 공지가 없습니다"));
    }
}

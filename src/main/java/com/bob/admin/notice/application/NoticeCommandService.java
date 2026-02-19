package com.bob.admin.notice.application;

import static com.bob.admin.notice.domain.Notice.createBanner;
import static java.time.LocalDateTime.now;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.admin.notice.application.dto.command.RegisterBannerCommand;
import com.bob.admin.notice.application.port.in.NoticeRegister;
import com.bob.admin.notice.domain.Notice;
import com.bob.admin.notice.domain.repository.NoticeRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeCommandService implements NoticeRegister {

    private final NoticeRepository noticeRepository;

    @Override
    public Notice registerBanner(RegisterBannerCommand command) {
        noticeRepository.findCurrentBanner(now(), PageRequest.of(0, 1)).stream()
            .findFirst()
            .ifPresent(Notice::deactivate);

        Notice notice = createBanner(command.writerId(), command.content(), command.endTime());

        return noticeRepository.save(notice);
    }
}

package com.bob.domain.notification.reader;

import com.bob.domain.notification.entity.Notification;
import com.bob.domain.notification.repository.NotiRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class NotiReader {

  private final NotiRepository notiRepository;

  public Notification readNotificationById(Long id) {
    return notiRepository.findById(id)
        .orElseThrow(() -> new ApplicationException(ApplicationError.NOT_EXISTS_NOTIFICATION));
  }

  public List<Notification> readNotifications(UUID memberId) {
    LocalDateTime time = LocalDateTime.now().minusWeeks(2);
    return notiRepository.findByReceiverIdAndCreatedAtAfter(memberId, time);
  }
}

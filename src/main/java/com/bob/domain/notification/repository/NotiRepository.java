package com.bob.domain.notification.repository;

import com.bob.domain.notification.entity.Notification;
import org.springframework.data.repository.CrudRepository;

public interface NotiRepository extends CrudRepository<Notification, Long> {

}

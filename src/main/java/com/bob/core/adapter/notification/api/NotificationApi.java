package com.bob.core.adapter.notification.api;

import static com.bob.core.adapter.common.symbol.ResponseSymbol.UPDATED;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bob.core.adapter.common.AuthenticationId;
import com.bob.core.adapter.common.CommonResponse;
import com.bob.core.adapter.common.symbol.ResponseSymbol;
import com.bob.core.adapter.notification.api.response.NotificationResponse;
import com.bob.core.application.notification.dto.command.MarkAsReadCommand;
import com.bob.core.application.notification.dto.query.ReadByMemberQuery;
import com.bob.core.application.notification.port.in.NotificationMarker;
import com.bob.core.application.notification.port.in.NotificationReader;
import com.bob.core.domain.notification.Notification;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
public class NotificationApi {

    private final NotificationReader notificationReader;
    private final NotificationMarker notificationMarker;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> readNotifications(@AuthenticationId UUID memberId) {
        ReadByMemberQuery query = new ReadByMemberQuery(memberId);
        List<Notification> notifications = notificationReader.readByMember(query);

        List<NotificationResponse> response = notifications.stream()
            .map(NotificationResponse::of)
            .toList();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{notificationId}")
    public CommonResponse<ResponseSymbol> markNotificationAsRead(
        @PathVariable Long notificationId,
        @AuthenticationId UUID memberId
    ) {
        MarkAsReadCommand command = new MarkAsReadCommand(memberId);

        notificationMarker.markAsRead(notificationId, command);

        return new CommonResponse<>(true, UPDATED);
    }

    @PatchMapping
    public CommonResponse<ResponseSymbol> markNotificationsAsRead(@AuthenticationId UUID memberId) {
        MarkAsReadCommand command = new MarkAsReadCommand(memberId);

        notificationMarker.markAllAsRead(command);

        return new CommonResponse<>(true, UPDATED);
    }
}

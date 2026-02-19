package com.bob.integration.adapter.notification;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.support.annotation.ContainerTest;

@DisplayName("알림 회원 조회 테스트")
@ContainerTest
record NotificationMemberAdapterTest(NotificationMemberAdapter notificationMemberAdapter) {

    @Test
    void 활성_회원_ID_목록_조회() {
        List<UUID> result = notificationMemberAdapter.readAllMemberIds();

        assertThat(result).hasSize(4);
        assertThat(result).containsExactlyInAnyOrder(
            UUID.fromString("0199f8c2-30ed-7ee3-a757-16196412518c"),
            UUID.fromString("019a6928-6f73-79f7-bd1b-6bfd4771a302"),
            UUID.fromString("019a6928-6f73-79f7-bd1b-6bfd4771a303"),
            UUID.fromString("021a6930-6f73-14c6-cf0a-2fd17132f102")
        );
    }
}

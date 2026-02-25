package com.bob.statistics.application.recorder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bob.statistics.application.port.out.StatisticsEntityStateStore;
import com.bob.statistics.domain.StatisticsMemberDailySnapshot;
import com.bob.statistics.domain.StatisticsMetricEvent;
import com.bob.statistics.domain.StatisticsPostDailySnapshot;
import com.bob.statistics.domain.StatisticsTradeDailySnapshot;
import com.bob.statistics.domain.repository.StatisticsMemberDailySnapshotRepository;
import com.bob.statistics.domain.repository.StatisticsPostDailySnapshotRepository;
import com.bob.statistics.domain.repository.StatisticsTradeDailySnapshotRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("생성일 기준 현재 상태 집계 테스트")
class StatisticsCohortCurrentSnapshotServiceTest {

    @Mock
    private StatisticsMemberDailySnapshotRepository memberRepository;

    @Mock
    private StatisticsPostDailySnapshotRepository postRepository;

    @Mock
    private StatisticsTradeDailySnapshotRepository tradeRepository;

    private StatisticsCohortCurrentSnapshotService service;

    private final Map<LocalDate, StatisticsMemberDailySnapshot> memberSnapshots = new HashMap<>();
    private final Map<LocalDate, StatisticsPostDailySnapshot> postSnapshots = new HashMap<>();
    private final Map<LocalDate, StatisticsTradeDailySnapshot> tradeSnapshots = new HashMap<>();

    @BeforeEach
    void setUp() {
        StatisticsEntityStateStore stateStore = new InMemoryEntityStateStore();
        service = new StatisticsCohortCurrentSnapshotService(stateStore, memberRepository, postRepository,
            tradeRepository);
    }

    @Test
    void 거래_상태_변경_시_이전상태를_감산_신규상태를_가산() {
        stubTradeRepository();
        LocalDate createdDate = LocalDate.of(2026, 2, 3);

        var created = new StatisticsMetricEvent(createdDate, "trade", "new_trades", 1, "trade-1", createdDate);
        var reserved = new StatisticsMetricEvent(LocalDate.of(2026, 2, 24), "trade", "status:RESERVED", 1, "trade-1",
            createdDate);
        var completed = new StatisticsMetricEvent(LocalDate.of(2026, 2, 26), "trade", "status:COMPLETED", 1, "trade-1",
            createdDate);

        service.apply(List.of(created, reserved, completed));

        StatisticsTradeDailySnapshot snapshot = tradeSnapshots.get(createdDate);
        assertThat(snapshot.getNewTrades()).isEqualTo(1);
        assertThat(snapshot.getReservedTrades()).isZero();
        assertThat(snapshot.getCompletedTrades()).isEqualTo(1);
    }

    @Test
    void 거래_모든_상태_지표_집계() {
        stubTradeRepository();
        LocalDate createdDate = LocalDate.of(2026, 2, 3);

        List<StatisticsMetricEvent> events = List.of(
            new StatisticsMetricEvent(LocalDate.of(2026, 2, 24), "trade", "status:REQUESTED", 1, "trade-2",
                createdDate),
            new StatisticsMetricEvent(LocalDate.of(2026, 2, 25), "trade", "status:ACCEPTED", 1, "trade-2", createdDate),
            new StatisticsMetricEvent(LocalDate.of(2026, 2, 26), "trade", "status:CANCELED", 1, "trade-2", createdDate)
        );

        service.apply(events);

        StatisticsTradeDailySnapshot snapshot = tradeSnapshots.get(createdDate);
        assertThat(snapshot.getRequestedTrades()).isZero();
        assertThat(snapshot.getAcceptedTrades()).isZero();
        assertThat(snapshot.getCanceledTrades()).isEqualTo(1);
    }

    @Test
    void 신규_지표는_동일_엔티티에_대해_한번만_집계() {
        stubMemberRepository();
        LocalDate createdDate = LocalDate.of(2026, 2, 3);

        var created = new StatisticsMetricEvent(createdDate, "member", "new_members", 1, "member-1", createdDate);
        service.apply(List.of(created, created));

        StatisticsMemberDailySnapshot snapshot = memberSnapshots.get(createdDate);
        assertThat(snapshot.getNewMembers()).isEqualTo(1);
    }

    @Test
    void 회원_상태_전이_집계() {
        stubMemberRepository();
        LocalDate createdDate = LocalDate.of(2026, 2, 3);

        var deactivated = new StatisticsMetricEvent(LocalDate.of(2026, 2, 24), "member", "status:DEACTIVATED", 1,
            "member-9", createdDate);
        var banned = new StatisticsMetricEvent(LocalDate.of(2026, 2, 25), "member", "status:BANNED", 1, "member-9",
            createdDate);

        service.apply(List.of(deactivated, banned));

        StatisticsMemberDailySnapshot snapshot = memberSnapshots.get(createdDate);
        assertThat(snapshot.getDeactivatedMembers()).isZero();
        assertThat(snapshot.getBannedMembers()).isEqualTo(1);
    }

    @Test
    void 동일_상태_중복_이벤트_한번만_집계() {
        stubTradeRepository();
        LocalDate createdDate = LocalDate.of(2026, 2, 3);

        var rejected = new StatisticsMetricEvent(LocalDate.of(2026, 2, 24), "trade", "status:REJECTED", 1, "trade-3",
            createdDate);

        service.apply(List.of(rejected, rejected));

        StatisticsTradeDailySnapshot snapshot = tradeSnapshots.get(createdDate);
        assertThat(snapshot.getRejectedTrades()).isEqualTo(1);
    }

    @Test
    void 게시글_삭제에서_활성_복구_시_삭제_카운트_감산() {
        stubPostRepository();
        LocalDate createdDate = LocalDate.of(2026, 2, 3);

        var deleted = new StatisticsMetricEvent(LocalDate.of(2026, 2, 24), "post", "deleted_posts", 1, "post-1",
            createdDate);
        var active = new StatisticsMetricEvent(LocalDate.of(2026, 2, 26), "post", "status:ACTIVE", 1, "post-1",
            createdDate);

        service.apply(List.of(deleted, active));

        StatisticsPostDailySnapshot snapshot = postSnapshots.get(createdDate);
        assertThat(snapshot.getDeletedPosts()).isZero();
        assertThat(snapshot.getBannedPosts()).isZero();
    }

    @Test
    void 게시글_제재_상태_집계() {
        stubPostRepository();
        LocalDate createdDate = LocalDate.of(2026, 2, 3);

        var banned = new StatisticsMetricEvent(LocalDate.of(2026, 2, 24), "post", "banned_posts", 1, "post-2",
            createdDate);

        service.apply(List.of(banned));

        StatisticsPostDailySnapshot snapshot = postSnapshots.get(createdDate);
        assertThat(snapshot.getBannedPosts()).isEqualTo(1);
    }

    @Test
    void 게시글_신규_지표_집계() {
        stubPostRepository();
        LocalDate createdDate = LocalDate.of(2026, 2, 3);

        var created = new StatisticsMetricEvent(createdDate, "post", "new_posts", 1, "post-3", createdDate);

        service.apply(List.of(created));

        StatisticsPostDailySnapshot snapshot = postSnapshots.get(createdDate);
        assertThat(snapshot.getNewPosts()).isEqualTo(1);
    }

    private void stubMemberRepository() {
        given(memberRepository.findBySnapshotDate(any(LocalDate.class)))
            .willAnswer(invocation -> Optional.ofNullable(memberSnapshots.get(invocation.getArgument(0))));

        given(memberRepository.save(any(StatisticsMemberDailySnapshot.class)))
            .willAnswer(invocation -> {
                StatisticsMemberDailySnapshot snapshot = invocation.getArgument(0);
                memberSnapshots.put(snapshot.getSnapshotDate(), snapshot);
                return snapshot;
            });
    }

    private void stubPostRepository() {
        given(postRepository.findBySnapshotDate(any(LocalDate.class)))
            .willAnswer(invocation -> Optional.ofNullable(postSnapshots.get(invocation.getArgument(0))));

        given(postRepository.save(any(StatisticsPostDailySnapshot.class)))
            .willAnswer(invocation -> {
                StatisticsPostDailySnapshot snapshot = invocation.getArgument(0);
                postSnapshots.put(snapshot.getSnapshotDate(), snapshot);
                return snapshot;
            });
    }

    private void stubTradeRepository() {
        given(tradeRepository.findBySnapshotDate(any(LocalDate.class)))
            .willAnswer(invocation -> Optional.ofNullable(tradeSnapshots.get(invocation.getArgument(0))));

        given(tradeRepository.save(any(StatisticsTradeDailySnapshot.class)))
            .willAnswer(invocation -> {
                StatisticsTradeDailySnapshot snapshot = invocation.getArgument(0);
                tradeSnapshots.put(snapshot.getSnapshotDate(), snapshot);
                return snapshot;
            });
    }

    private static class InMemoryEntityStateStore implements StatisticsEntityStateStore {

        private final Map<String, EntityState> store = new HashMap<>();

        @Override
        public Optional<EntityState> read(String domain, String entityId) {
            return Optional.ofNullable(store.get(domain + ":" + entityId));
        }

        @Override
        public void save(EntityState state) {
            store.put(state.domain() + ":" + state.entityId(), state);
        }
    }
}

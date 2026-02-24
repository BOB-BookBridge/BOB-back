package com.bob.statistics.application;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.statistics.application.port.out.StatisticsEntityStateStore;
import com.bob.statistics.application.port.out.StatisticsEntityStateStore.EntityState;
import com.bob.statistics.domain.StatisticsMemberDailySnapshot;
import com.bob.statistics.domain.StatisticsMetricEvent;
import com.bob.statistics.domain.StatisticsPostDailySnapshot;
import com.bob.statistics.domain.StatisticsTradeDailySnapshot;
import com.bob.statistics.domain.repository.StatisticsMemberDailySnapshotRepository;
import com.bob.statistics.domain.repository.StatisticsPostDailySnapshotRepository;
import com.bob.statistics.domain.repository.StatisticsTradeDailySnapshotRepository;

@Service
@RequiredArgsConstructor
public class StatisticsCohortCurrentSnapshotService {

    private final StatisticsEntityStateStore stateStore;
    private final StatisticsMemberDailySnapshotRepository memberRepository;
    private final StatisticsPostDailySnapshotRepository postRepository;
    private final StatisticsTradeDailySnapshotRepository tradeRepository;

    @Transactional
    public void apply(List<StatisticsMetricEvent> events) {
        for (StatisticsMetricEvent event : events)
            applyEvent(event);
    }

    private void applyEvent(StatisticsMetricEvent event) {
        EntityState state = stateStore.read(event.domain(), event.entityId())
            .orElse(new EntityState(event.domain(), event.entityId(), event.cohortDate(), null, false));

        if (event.metric().startsWith("new_")) {
            if (state.createdCounted())
                return;

            incrementNew(event.domain(), event.cohortDate(), event.value());
            stateStore.save(new EntityState(
                state.domain(), state.entityId(), state.cohortDate(), state.currentStatus(), true
            ));

            return;
        }

        String nextStatus = toStatus(event.domain(), event.metric());
        if (Objects.equals(state.currentStatus(), nextStatus))
            return;

        Optional.ofNullable(toCounterMetric(event.domain(), state.currentStatus()))
            .ifPresent(metric -> adjustCurrent(event.domain(), event.cohortDate(), metric, -event.value()));

        Optional.ofNullable(toCounterMetric(event.domain(), nextStatus))
            .ifPresent(metric -> adjustCurrent(event.domain(), event.cohortDate(), metric, event.value()));

        stateStore.save(new EntityState(
            state.domain(), state.entityId(), state.cohortDate(), nextStatus, state.createdCounted()
        ));
    }

    private void incrementNew(String domain, LocalDate cohortDate, long value) {
        switch (domain) {
            case "member" -> {
                StatisticsMemberDailySnapshot snapshot = memberRepository.findBySnapshotDate(cohortDate)
                    .orElse(StatisticsMemberDailySnapshot.createEmpty(cohortDate));

                snapshot.addNewMembers(value);
                memberRepository.save(snapshot);
            }
            case "post" -> {
                StatisticsPostDailySnapshot snapshot = postRepository.findBySnapshotDate(cohortDate)
                    .orElse(StatisticsPostDailySnapshot.createEmpty(cohortDate));

                snapshot.addNewPosts(value);
                postRepository.save(snapshot);
            }
            case "trade" -> {
                StatisticsTradeDailySnapshot snapshot = tradeRepository.findBySnapshotDate(cohortDate)
                    .orElse(StatisticsTradeDailySnapshot.createEmpty(cohortDate));

                snapshot.addNewTrades(value);
                tradeRepository.save(snapshot);
            }
            default -> throw new IllegalArgumentException("Unsupported domain: " + domain);
        }
    }

    private void adjustCurrent(String domain, LocalDate cohortDate, String metric, long value) {
        switch (domain) {
            case "member" -> {
                StatisticsMemberDailySnapshot snapshot = memberRepository.findBySnapshotDate(cohortDate)
                    .orElse(StatisticsMemberDailySnapshot.createEmpty(cohortDate));
                if ("deactivated_members".equals(metric))
                    snapshot.addDeactivatedMembers(value);
                else if ("banned_members".equals(metric))
                    snapshot.addBannedMembers(value);

                memberRepository.save(snapshot);
            }
            case "post" -> {
                StatisticsPostDailySnapshot snapshot = postRepository.findBySnapshotDate(cohortDate)
                    .orElse(StatisticsPostDailySnapshot.createEmpty(cohortDate));
                if ("deleted_posts".equals(metric))
                    snapshot.addDeletedPosts(value);
                else if ("banned_posts".equals(metric))
                    snapshot.addBannedPosts(value);

                postRepository.save(snapshot);
            }
            case "trade" -> {
                StatisticsTradeDailySnapshot snapshot = tradeRepository.findBySnapshotDate(cohortDate)
                    .orElse(StatisticsTradeDailySnapshot.createEmpty(cohortDate));

                switch (metric) {
                    case "requested_trades" -> snapshot.addRequestedTrades(value);
                    case "accepted_trades" -> snapshot.addAcceptedTrades(value);
                    case "rejected_trades" -> snapshot.addRejectedTrades(value);
                    case "canceled_trades" -> snapshot.addCanceledTrades(value);
                    case "reserved_trades" -> snapshot.addReservedTrades(value);
                    case "completed_trades" -> snapshot.addCompletedTrades(value);
                    default -> throw new IllegalArgumentException("Unsupported trade metric: " + metric);
                }
                tradeRepository.save(snapshot);
            }
            default -> throw new IllegalArgumentException("Unsupported domain: " + domain);
        }
    }

    private static String toStatus(String domain, String metric) {
        if ("member".equals(domain) || "trade".equals(domain))
            return metric.substring("status:".length());

        if ("post".equals(domain)) {
            if ("deleted_posts".equals(metric))
                return "DEACTIVATED";

            if ("banned_posts".equals(metric))
                return "BANNED";

            if ("status:ACTIVE".equals(metric))
                return "ACTIVE";
        }
        throw new IllegalArgumentException("Unsupported domain or metric. domain=" + domain + ", metric=" + metric);
    }

    private static String toCounterMetric(String domain, String status) {
        if (status == null)
            return null;

        return switch (domain) {
            case "member" -> switch (status) {
                case "DEACTIVATED" -> "deactivated_members";
                case "BANNED" -> "banned_members";
                default -> null;
            };
            case "post" -> switch (status) {
                case "DEACTIVATED" -> "deleted_posts";
                case "BANNED" -> "banned_posts";
                default -> null;
            };
            case "trade" -> switch (status) {
                case "REQUESTED" -> "requested_trades";
                case "ACCEPTED" -> "accepted_trades";
                case "REJECTED" -> "rejected_trades";
                case "CANCELED" -> "canceled_trades";
                case "RESERVED" -> "reserved_trades";
                case "COMPLETED" -> "completed_trades";
                default -> null;
            };
            default -> null;
        };
    }
}

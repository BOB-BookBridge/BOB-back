package com.bob.statistics.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.bob.core.member.domain.Member;
import com.bob.core.post.domain.Post;
import com.bob.core.post.domain.status.Status;
import com.bob.core.trade.application.dto.result.ChangeTradeStatusResult;
import com.bob.core.trade.domain.Trade;
import com.bob.statistics.application.port.in.StatisticsSnapshotRecorder;
import com.bob.statistics.application.port.out.StatisticsEntityStateStore;
import com.bob.statistics.application.port.out.StatisticsEntityStateStore.EntityState;
import com.bob.statistics.application.port.out.StatisticsMetricStore;
import com.bob.statistics.domain.StatisticsMetricEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsSnapshotRecordService implements StatisticsSnapshotRecorder {

    private final StatisticsMetricStore metricStore;
    private final StatisticsEntityStateStore entityStateStore;
    private final StatisticsCohortCurrentSnapshotService cohortCurrentSnapshotService;

    @Override
    public void record(Object target, LocalDateTime txStartedAt) {
        record(target, txStartedAt, false);
    }

    @Override
    public void record(Object target, LocalDateTime txStartedAt, boolean onlyCreate) {
        List<StatisticsMetricEvent> events = convertToMetricEvents(target, txStartedAt, onlyCreate);
        if (events.isEmpty()) {
            log.debug("statistics snapshot skipped. targetType={}, txStartedAt={}, onlyCreate={}",
                target == null ? "null" : target.getClass().getSimpleName(), txStartedAt, onlyCreate);
            return;
        }

        List<StatisticsMetricEvent> metricStoreEvents = eventsForMetricStore(events, txStartedAt.toLocalDate());
        if (!metricStoreEvents.isEmpty())
            metricStore.saveAll(metricStoreEvents, txStartedAt);

        List<StatisticsMetricEvent> cohortEvents = eventsForCohortSnapshot(events, txStartedAt.toLocalDate());
        if (!cohortEvents.isEmpty())
            cohortCurrentSnapshotService.apply(cohortEvents);

        log.debug("statistics snapshot converted. targetType={}, txStartedAt={}, onlyCreate={}, events={}",
            target.getClass().getSimpleName(), txStartedAt, onlyCreate, events);
    }

    private List<StatisticsMetricEvent> eventsForMetricStore(List<StatisticsMetricEvent> events, LocalDate txDate) {
        List<StatisticsMetricEvent> result = new ArrayList<>();
        for (StatisticsMetricEvent event : events) {
            if (!event.cohortDate().isEqual(txDate))
                continue;

            if ("member".equals(event.domain())) {
                result.add(event);
                continue;
            }

            if (event.metric().startsWith("new_")) {
                result.add(event);
                continue;
            }

            String nextStatus = toStatus(event.domain(), event.metric());
            EntityState previousState = entityStateStore.read(event.domain(), event.entityId()).orElse(null);
            String previousStatus = previousState == null ? null : previousState.currentStatus();
            if (Objects.equals(previousStatus, nextStatus)) {
                continue;
            }

            String previousMetric = toCounterMetric(event.domain(), previousStatus);
            if (previousMetric != null) {
                result.add(new StatisticsMetricEvent(
                    event.eventDate(), event.domain(), previousMetric, -1, event.entityId(), event.cohortDate()
                ));
            }

            String nextMetric = toCounterMetric(event.domain(), nextStatus);
            if (nextMetric != null) {
                result.add(new StatisticsMetricEvent(
                    event.eventDate(), event.domain(), nextMetric, 1, event.entityId(), event.cohortDate()
                ));
            }
        }

        return result;
    }

    private static List<StatisticsMetricEvent> eventsForCohortSnapshot(List<StatisticsMetricEvent> events,
        LocalDate txDate
    ) {
        return events.stream()
            .filter(event -> event.cohortDate().isBefore(txDate))
            .toList();
    }

    private static String toStatus(String domain, String metric) {
        if ("trade".equals(domain) || "member".equals(domain))
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
            case "trade", "member" -> "status:" + status;
            case "post" -> switch (status) {
                case "DEACTIVATED" -> "deleted_posts";
                case "BANNED" -> "banned_posts";
                default -> null;
            };
            default -> null;
        };
    }

    List<StatisticsMetricEvent> convertToMetricEvents(Object target, LocalDateTime txStartedAt, boolean onlyCreate) {
        Objects.requireNonNull(target, "statistics snapshot target must not be null");

        if (target instanceof ChangeTradeStatusResult changeTradeStatusResult)
            return toTradeEvents(changeTradeStatusResult.trade(), txStartedAt);

        if (target instanceof Collection<?> collection)
            return convertCollectionToMetricEvents(collection, txStartedAt, onlyCreate);

        if (target instanceof Member member)
            return toMemberEvents(member, txStartedAt, onlyCreate);

        if (target instanceof Post post)
            return toPostEvents(post, txStartedAt, false);

        if (target instanceof Trade trade)
            return toTradeEvents(trade, txStartedAt);

        throw new IllegalArgumentException("Unsupported statistics target type: " + target.getClass().getName());
    }

    private List<StatisticsMetricEvent> convertCollectionToMetricEvents(
        Collection<?> collection,
        LocalDateTime txStartedAt,
        boolean onlyCreate
    ) {
        List<StatisticsMetricEvent> events = new ArrayList<>();
        for (Object element : collection) {
            if (element instanceof Post post) {
                events.addAll(toPostEvents(post, txStartedAt, true));
                continue;
            }
            events.addAll(convertToMetricEvents(element, txStartedAt, onlyCreate));
        }

        return events;
    }

    private List<StatisticsMetricEvent> toMemberEvents(Member member, LocalDateTime txStartedAt, boolean onlyCreate) {
        List<StatisticsMetricEvent> events = new ArrayList<>();

        LocalDate eventDate = txStartedAt.toLocalDate();
        LocalDate cohortDate = toDate(member.getCreatedAt());
        String entityId = member.getId().toString();
        boolean created = isCreated(member.getCreatedAt(), txStartedAt);

        if (created)
            events.add(metric(eventDate, "member", "new_members", entityId, cohortDate));

        boolean active = member.getStatus() == com.bob.core.member.domain.Status.ACTIVE;
        if (!onlyCreate && (!active || !created))
            events.add(metric(eventDate, "member", "status:" + member.getStatus().name(), entityId, cohortDate));

        return events;
    }

    private List<StatisticsMetricEvent> toPostEvents(Post post, LocalDateTime txStartedAt, boolean allowActiveStatus) {
        List<StatisticsMetricEvent> events = new ArrayList<>();

        LocalDate eventDate = txStartedAt.toLocalDate();
        LocalDate cohortDate = toDate(post.getCreatedAt());
        String entityId = String.valueOf(post.getId());
        boolean created = isCreated(post.getCreatedAt(), txStartedAt);

        if (created)
            events.add(metric(eventDate, "post", "new_posts", entityId, cohortDate));

        if (post.getStatus() == Status.DEACTIVATED)
            events.add(metric(eventDate, "post", "deleted_posts", entityId, cohortDate));

        if (post.getStatus() == Status.BANNED)
            events.add(metric(eventDate, "post", "banned_posts", entityId, cohortDate));

        if (allowActiveStatus && !created && post.getStatus() == Status.ACTIVE)
            events.add(metric(eventDate, "post", "status:ACTIVE", entityId, cohortDate));

        return events;
    }

    private List<StatisticsMetricEvent> toTradeEvents(Trade trade, LocalDateTime txStartedAt) {
        List<StatisticsMetricEvent> events = new ArrayList<>();

        LocalDate eventDate = txStartedAt.toLocalDate();
        LocalDate cohortDate = toDate(trade.getCreatedAt());
        String entityId = String.valueOf(trade.getId());
        boolean created = isCreated(trade.getCreatedAt(), txStartedAt);
        if (created)
            events.add(metric(eventDate, "trade", "new_trades", entityId, cohortDate));

        events.add(metric(eventDate, "trade", "status:" + trade.getStatus().name(), entityId, cohortDate));

        return events;
    }

    private static StatisticsMetricEvent metric(LocalDate eventDate, String domain, String metric, String entityId,
        LocalDate cohortDate
    ) {
        return new StatisticsMetricEvent(eventDate, domain, metric, 1, entityId, cohortDate);
    }

    private static boolean isCreated(LocalDateTime createdAt, LocalDateTime txStartedAt) {
        return !createdAt.isBefore(txStartedAt);
    }

    private static LocalDate toDate(LocalDateTime dateTime) {
        return dateTime.toLocalDate();
    }
}

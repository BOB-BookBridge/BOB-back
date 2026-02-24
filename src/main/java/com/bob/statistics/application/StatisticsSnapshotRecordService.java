package com.bob.statistics.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.bob.core.member.domain.Member;
import com.bob.core.post.domain.Post;
import com.bob.core.post.domain.status.Status;
import com.bob.core.trade.application.dto.result.ChangeTradeStatusResult;
import com.bob.core.trade.domain.Trade;
import com.bob.statistics.application.port.in.StatisticsSnapshotRecorder;
import com.bob.statistics.domain.StatisticsMetricEvent;

@Slf4j
@Service
public class StatisticsSnapshotRecordService implements StatisticsSnapshotRecorder {

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

        // TODO: 자정 경계(KST) 기준 날짜/버킷 계산 유틸 적용
        // TODO: Redis 일일 키(stats:daily:{domain}:{yyyyMMdd})에 집계 반영
        // TODO: 당일 10분 버킷 키(stats:realtime:{domain}:{yyyyMMdd}:{HHmm})에 집계 반영
        // TODO: cohortDate(생성일) 기준 조회 필터를 적용해 "기간 내 생성 거래" 통계를 보장
        // TODO: 중복 카운팅 방지 규칙(생성/상태변경/삭제 이벤트) 적용
        // TODO: Redis 저장 실패 시 재시도/로깅 등 보완
        log.debug("statistics snapshot converted. targetType={}, txStartedAt={}, onlyCreate={}, events={}",
            target.getClass().getSimpleName(), txStartedAt, onlyCreate, events);
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
            return toPostEvents(post, txStartedAt);
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
            events.addAll(convertToMetricEvents(element, txStartedAt, onlyCreate));
        }
        return events;
    }

    private List<StatisticsMetricEvent> toMemberEvents(Member member, LocalDateTime txStartedAt, boolean onlyCreate) {
        List<StatisticsMetricEvent> events = new ArrayList<>();

        LocalDate eventDate = txStartedAt.toLocalDate();
        LocalDate cohortDate = toDate(member.getCreatedAt());
        String entityId = member.getId() == null ? null : member.getId().toString();
        boolean created = isCreated(member.getCreatedAt(), txStartedAt);

        if (created)
            events.add(metric(eventDate, "member", "new_members", entityId, cohortDate));

        boolean active = member.getStatus() == com.bob.core.member.domain.Status.ACTIVE;
        if (!onlyCreate && (!active || !created))
            events.add(metric(eventDate, "member", "status:" + member.getStatus().name(), entityId, cohortDate));

        return events;
    }

    private List<StatisticsMetricEvent> toPostEvents(Post post, LocalDateTime txStartedAt) {
        List<StatisticsMetricEvent> events = new ArrayList<>();

        LocalDate eventDate = txStartedAt.toLocalDate();
        LocalDate cohortDate = toDate(post.getCreatedAt());
        String entityId = post.getId() == null ? null : String.valueOf(post.getId());
        boolean created = isCreated(post.getCreatedAt(), txStartedAt);

        if (created)
            events.add(metric(eventDate, "post", "new_posts", entityId, cohortDate));

        if (post.getStatus() == Status.DEACTIVATED)
            events.add(metric(eventDate, "post", "deleted_posts", entityId, cohortDate));

        if (post.getStatus() == Status.BANNED)
            events.add(metric(eventDate, "post", "banned_posts", entityId, cohortDate));

        return events;
    }

    private List<StatisticsMetricEvent> toTradeEvents(Trade trade, LocalDateTime txStartedAt) {
        List<StatisticsMetricEvent> events = new ArrayList<>();

        LocalDate eventDate = txStartedAt.toLocalDate();
        LocalDate cohortDate = toDate(trade.getCreatedAt());
        String entityId = trade.getId() == null ? null : String.valueOf(trade.getId());
        boolean created = isCreated(trade.getCreatedAt(), txStartedAt);
        boolean requested = "REQUESTED".equals(trade.getStatus().name());

        if (created)
            events.add(metric(eventDate, "trade", "new_trades", entityId, cohortDate));

        if (!requested || created)
            events.add(metric(eventDate, "trade", "status:" + trade.getStatus().name(), entityId, cohortDate));

        return events;
    }

    private static StatisticsMetricEvent metric(LocalDate eventDate, String domain, String metric, String entityId,
        LocalDate cohortDate
    ) {
        return new StatisticsMetricEvent(eventDate, domain, metric, 1, entityId, cohortDate);
    }

    private static boolean isCreated(LocalDateTime createdAt, LocalDateTime txStartedAt) {
        return createdAt != null && !createdAt.isBefore(txStartedAt);
    }

    private static LocalDate toDate(LocalDateTime dateTime) {
        if (Objects.isNull(dateTime))
            return null;
        return dateTime.toLocalDate();
    }
}

package com.bob.statistics.application;

import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.bob.statistics.application.port.in.StatisticsSnapshotRecorder;

@Slf4j
@Service
public class StatisticsSnapshotRecordService implements StatisticsSnapshotRecorder {

    @Override
    public void record(Object target, LocalDateTime txStartedAt) {
        // TODO: 자정 경계(KST) 기준 날짜/버킷 계산 유틸 적용
        // TODO: Redis 일일 키(stats:daily:{domain}:{yyyyMMdd})에 집계 반영
        // TODO: 당일 10분 버킷 키(stats:realtime:{domain}:{yyyyMMdd}:{HHmm})에 집계 반영
        // TODO: 중복 카운팅 방지 규칙(생성/상태변경/삭제 이벤트) 적용
        // TODO: target 타입(Member/Post/Trade/Report)별 분기 처리
        // TODO: 공통 통계 이벤트 모델로 변환 (eventDate, domain, metric, value)

        // 미래 적용사항
        // TODO: Redis 저장 실패 시 대응(재시도, 로깅, ...) 정책 적용
        log.debug("statistics snapshot recorded. targetType={}, txStartedAt={}",
            target == null ? "null" : target.getClass().getSimpleName(), txStartedAt);
    }
}

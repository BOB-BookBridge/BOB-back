package com.bob.global.snapshot;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Aspect
@Component
@RequiredArgsConstructor
public class RecordSnapshotAspect {

    private final SnapshotRecorder snapshotRecorder;

    @Around("@annotation(com.bob.global.snapshot.RecordSnapshot)")
    public Object recordAfterCommit(ProceedingJoinPoint joinPoint) throws Throwable {
        LocalDateTime txStartedAt = LocalDateTime.now();
        Object result = joinPoint.proceed();

        if (result == null)
            return null;

        if (!TransactionSynchronizationManager.isSynchronizationActive()
            || !TransactionSynchronizationManager.isActualTransactionActive()
        ) {
            snapshotRecorder.record(result, txStartedAt);
            return result;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                snapshotRecorder.record(result, txStartedAt);
            }
        });

        return result;
    }
}

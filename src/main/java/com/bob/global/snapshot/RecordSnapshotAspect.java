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

    @Around("@annotation(recordSnapshot)")
    public Object recordAfterCommit(ProceedingJoinPoint joinPoint, RecordSnapshot recordSnapshot) throws Throwable {
        LocalDateTime txStartedAt = LocalDateTime.now();
        Object result = joinPoint.proceed();
        boolean onlyCreate = recordSnapshot.onlyCreate();

        if (result == null)
            return null;

        if (!TransactionSynchronizationManager.isSynchronizationActive()
            || !TransactionSynchronizationManager.isActualTransactionActive()
        ) {
            throw new IllegalStateException("@RecordSnapshot requires an active transaction");
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                snapshotRecorder.record(result, txStartedAt, onlyCreate);
            }
        });

        return result;
    }
}

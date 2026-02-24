package com.bob.global.snapshot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.time.LocalDateTime;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@ExtendWith(MockitoExtension.class)
@DisplayName("스냅샷 기록 AOP 테스트")
class RecordSnapshotAspectTest {

    @Mock
    private SnapshotRecorder snapshotRecorder;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private RecordSnapshot recordSnapshot;

    @AfterEach
    void tearDown() {
        if (TransactionSynchronizationManager.isSynchronizationActive())
            TransactionSynchronizationManager.clearSynchronization();
        TransactionSynchronizationManager.setActualTransactionActive(false);
    }

    @Test
    void 반환값이_null이면_기록하지_않는다() throws Throwable {
        RecordSnapshotAspect aspect = new RecordSnapshotAspect(snapshotRecorder);
        given(joinPoint.proceed()).willReturn(null);
        given(recordSnapshot.onlyCreate()).willReturn(false);

        Object result = aspect.recordAfterCommit(joinPoint, recordSnapshot);

        assertThat(result).isNull();
        then(snapshotRecorder).shouldHaveNoInteractions();
    }

    @Test
    void 트랜잭션이_비활성_시_예외가_발생한다() throws Throwable {
        RecordSnapshotAspect aspect = new RecordSnapshotAspect(snapshotRecorder);
        Object target = new Object();
        given(joinPoint.proceed()).willReturn(target);
        given(recordSnapshot.onlyCreate()).willReturn(true);

        assertThatThrownBy(() -> aspect.recordAfterCommit(joinPoint, recordSnapshot))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("@RecordSnapshot requires an active transaction");

        then(snapshotRecorder).shouldHaveNoInteractions();
    }

    @Test
    void 동기화_활성_및_트랜잭션_미실행_시_예외가_발생한다() throws Throwable {
        RecordSnapshotAspect aspect = new RecordSnapshotAspect(snapshotRecorder);
        Object target = new Object();
        TransactionSynchronizationManager.initSynchronization();
        TransactionSynchronizationManager.setActualTransactionActive(false);
        given(joinPoint.proceed()).willReturn(target);
        given(recordSnapshot.onlyCreate()).willReturn(false);

        assertThatThrownBy(() -> aspect.recordAfterCommit(joinPoint, recordSnapshot))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("@RecordSnapshot requires an active transaction");

        then(snapshotRecorder).shouldHaveNoInteractions();
    }

    @Test
    void transaction_afterCommit_기록() throws Throwable {
        RecordSnapshotAspect aspect = new RecordSnapshotAspect(snapshotRecorder);
        Object target = new Object();
        TransactionSynchronizationManager.initSynchronization();
        TransactionSynchronizationManager.setActualTransactionActive(true);
        given(joinPoint.proceed()).willReturn(target);
        given(recordSnapshot.onlyCreate()).willReturn(false);

        Object result = aspect.recordAfterCommit(joinPoint, recordSnapshot);

        assertThat(result).isSameAs(target);
        then(snapshotRecorder).shouldHaveNoInteractions();
        assertThat(TransactionSynchronizationManager.getSynchronizations()).hasSize(1);

        for (TransactionSynchronization synchronization : TransactionSynchronizationManager.getSynchronizations()) {
            synchronization.afterCommit();
        }

        then(snapshotRecorder).should().record(eq(target), any(LocalDateTime.class), eq(false));
    }
}

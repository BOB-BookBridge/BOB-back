package com.bob.modulith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.support.TransactionTemplate;

import com.bob.support.container.TestContainersInitializer;

@SpringBootTest
@ContextConfiguration(initializers = TestContainersInitializer.class)
class ModulithEventTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestEventListener testListener;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private IncompleteEventPublications incompleteEvents;

    @Test
    void modulith_이벤트_처리() {
        String eventId = UUID.randomUUID().toString();
        TestEvent event = new TestEvent(eventId);

        transactionTemplate.execute(status -> {
            eventPublisher.publishEvent(event);
            return null;
        });

        await().atMost(Duration.ofSeconds(3)).untilAsserted(() -> {
            assertThat(testListener.getCallCount()).isGreaterThan(0);
            assertThat(testListener.getLastEventId()).isEqualTo(eventId);
        });

        await().atMost(Duration.ofSeconds(3))
            .untilAsserted(() -> assertThat(countCompletedEvent()).isGreaterThan(0));
    }

    @Test
    void modulith_이벤트_실패_후_재시도_처리() {
        testListener.setFailCount(3); // 3회 실패 설정
        String eventId = UUID.randomUUID().toString();
        TestEvent event = new TestEvent(eventId);

        transactionTemplate.execute(status -> {
            eventPublisher.publishEvent(event);
            return null;
        });

        await().atMost(Duration.ofSeconds(3))
            .untilAsserted(() -> assertThat(countFailureEvent()).isGreaterThan(0));

        /* ==== 재시도 ==== */
        testListener.setFailCount(0);
        incompleteEvents.resubmitIncompletePublications(p -> true);

        await().atMost(Duration.ofSeconds(5))
            .untilAsserted(() -> {
                assertThat(testListener.getCallCount()).isGreaterThan(0);
                assertThat(testListener.getLastEventId()).isEqualTo(eventId);

                assertThat(countCompletedEvent()).isGreaterThan(0);
            });
    }

    private @Nullable Integer countCompletedEvent() {
        return jdbcTemplate.queryForObject("""
            SELECT COUNT(*) 
            FROM event_publication 
            WHERE completion_date IS NOT NULL
            """, Integer.class);
    }

    private @Nullable Integer countFailureEvent() {
        return jdbcTemplate.queryForObject("""
            SELECT COUNT(*) 
            FROM event_publication 
            WHERE completion_date IS NULL
            """, Integer.class);
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public TestEventListener testEventListener() {
            return new TestEventListener();
        }
    }

    @Getter
    static class TestEventListener {

        private final AtomicInteger callCount = new AtomicInteger(0);
        private final AtomicInteger attemptCount = new AtomicInteger(0);
        private volatile String lastEventId;
        private volatile int failCount = 0;

        @ApplicationModuleListener
        public void handle(TestEvent event) {
            int attempt = attemptCount.incrementAndGet();

            if (attempt <= failCount) {
                throw new RuntimeException("Test failure (attempt " + attempt + ")");
            }

            callCount.incrementAndGet();
            this.lastEventId = event.id();
        }

        public int getCallCount() {
            return callCount.get();
        }

        public void setFailCount(int failCount) {
            this.failCount = failCount;
            this.attemptCount.set(0);
        }
    }

    record TestEvent(String id) {

    }
}

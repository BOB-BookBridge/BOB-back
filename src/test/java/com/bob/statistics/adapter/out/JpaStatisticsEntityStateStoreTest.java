package com.bob.statistics.adapter.out;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bob.statistics.application.port.out.StatisticsEntityStateStore.EntityState;
import com.bob.statistics.domain.StatisticsEntityState;
import com.bob.statistics.domain.repository.StatisticsEntityStateRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Entity 상태 저장소 테스트")
class JpaStatisticsEntityStateStoreTest {

    @InjectMocks
    private JpaStatisticsEntityStateStore store;

    @Mock
    private StatisticsEntityStateRepository repository;

    @Test
    void 상태_조회() {
        StatisticsEntityState entity = StatisticsEntityState.builder()
            .domain("post")
            .entityId("post-1")
            .cohortDate(LocalDate.of(2026, 2, 3))
            .currentStatus("BANNED")
            .createdCounted(true)
            .build();

        given(repository.findByDomainAndEntityId("post", "post-1")).willReturn(Optional.of(entity));

        Optional<EntityState> state = store.read("post", "post-1");

        assertThat(state).isPresent();
        assertThat(state.get().domain()).isEqualTo("post");
        assertThat(state.get().entityId()).isEqualTo("post-1");
        assertThat(state.get().cohortDate()).isEqualTo(LocalDate.of(2026, 2, 3));
        assertThat(state.get().currentStatus()).isEqualTo("BANNED");
        assertThat(state.get().createdCounted()).isTrue();
    }

    @Test
    void 상태_미존재_시_빈값_반환() {
        given(repository.findByDomainAndEntityId("trade", "trade-9")).willReturn(Optional.empty());

        Optional<EntityState> state = store.read("trade", "trade-9");

        assertThat(state).isEmpty();
    }

    @Test
    void 기존_상태_갱신() {
        StatisticsEntityState existing = StatisticsEntityState.builder()
            .domain("trade")
            .entityId("trade-1")
            .cohortDate(LocalDate.of(2026, 2, 3))
            .currentStatus("REQUESTED")
            .createdCounted(false)
            .build();

        given(repository.findByDomainAndEntityId("trade", "trade-1")).willReturn(Optional.of(existing));

        EntityState state = new EntityState("trade", "trade-1", LocalDate.of(2026, 2, 3), "COMPLETED", true);
        store.save(state);

        then(repository).should().save(existing);
        assertThat(existing.getCurrentStatus()).isEqualTo("COMPLETED");
        assertThat(existing.isCreatedCounted()).isTrue();
        assertThat(existing.getUpdatedAt()).isNotNull();
    }

    @Test
    void 신규_상태_생성() {
        given(repository.findByDomainAndEntityId("member", "member-1")).willReturn(Optional.empty());

        EntityState state = new EntityState("member", "member-1", LocalDate.of(2026, 2, 4), "DEACTIVATED", true);
        store.save(state);

        ArgumentCaptor<StatisticsEntityState> captor = ArgumentCaptor.forClass(StatisticsEntityState.class);
        then(repository).should().save(captor.capture());

        StatisticsEntityState saved = captor.getValue();
        assertThat(saved.getDomain()).isEqualTo("member");
        assertThat(saved.getEntityId()).isEqualTo("member-1");
        assertThat(saved.getCohortDate()).isEqualTo(LocalDate.of(2026, 2, 4));
        assertThat(saved.getCurrentStatus()).isEqualTo("DEACTIVATED");
        assertThat(saved.isCreatedCounted()).isTrue();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }
}

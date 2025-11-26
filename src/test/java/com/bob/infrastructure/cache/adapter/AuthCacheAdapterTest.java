package com.bob.infrastructure.cache.adapter;

import static com.bob.global.utils.random.RandomUtils.generateCode;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.infrastructure.cache.repository.impl.RedisKeyValueRepository;
import com.bob.support.annotation.ContainerTest;

@DisplayName("인증 캐시 활용 테스트")
@ContainerTest
record AuthCacheAdapterTest(AuthCacheAdapter adapter, RedisKeyValueRepository repository) {

    @AfterEach
    void tearDown() {
        repository.clear();
    }

    @Test
    void 재발급_키_저장() {
        String key = generateCode(32);
        String value = MEMBER_ID.toString();

        adapter.setRefreshKey(key, value);

        String save = repository.get(convertKey(key)).orElseThrow();
        assertThat(save).isEqualTo(value);
    }

    @Test
    void 재발급_키_업데이트() {
        String old = generateCode(32);
        String value = MEMBER_ID.toString();
        adapter.setRefreshKey(old, value);
        assertThat(repository.exists(convertKey(old))).isTrue();

        String current = generateCode(32);

        adapter.updateRefreshKey(old, current, value);

        String cover = repository.get(convertKey(current)).orElseThrow();
        assertThat(repository.exists(convertKey(old))).isFalse();
        assertThat(cover).isEqualTo(value);
    }

    @Test
    void 조회() {
        String key = generateCode(32);
        String value = MEMBER_ID.toString();
        adapter.setRefreshKey(key, value);

        Optional<String> result = adapter.get(key);

        assertThat(result).isEqualTo(Optional.of(value));
    }

    private String convertKey(String refresh) {
        return "refresh:" + refresh;
    }
}

package com.bob.core.application.interest.port.in;

import static com.bob.global.exception.response.ApplicationError.INTEREST_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.application.interest.dto.query.FindInterestByNameQuery;
import com.bob.core.domain.interest.Interest;
import com.bob.core.domain.interest.repository.InterestRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.annotation.ContainerTest;

@DisplayName("관심사 조회 테스트")
@Transactional
@ContainerTest
record InterestReaderTest(InterestReader interestReader, InterestRepository interestRepository) {

    @Test
    void 관심사_조회() {
        Interest interest = Interest.createInterest("java");
        interestRepository.save(interest);

        Interest result = interestReader.read(interest.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(interest.getId());
        assertThat(result.getName()).isEqualTo("java");
    }

    @Test
    void 관심사_조회_시_존재하지_않으면_사용자_예외가_발생한다() {
        Long nonExistentId = 999L;

        assertThatThrownBy(() -> interestReader.read(nonExistentId))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(INTEREST_NOT_FOUND.getMessage());
    }

    @Test
    void 이름_기반_관심사_조회() {
        Interest interest = Interest.createInterest("java");
        interestRepository.save(interest);

        FindInterestByNameQuery query = FindInterestByNameQuery.of("java");
        Interest result = interestReader.readByName(query);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(interest.getId());
        assertThat(result.getName()).isEqualTo("java");
    }

    @Test
    void 이름_기반_관심사_조회_시_존재하지_않으면_사용자_예외가_발생한다() {
        FindInterestByNameQuery query = FindInterestByNameQuery.of("nonexistent");

        assertThatThrownBy(() -> interestReader.readByName(query))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(INTEREST_NOT_FOUND.getMessage());
    }
}

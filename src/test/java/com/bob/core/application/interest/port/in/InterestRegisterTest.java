package com.bob.core.application.interest.port.in;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.application.interest.dto.command.RegisterInterestsByNamesCommand;
import com.bob.core.domain.interest.Interest;
import com.bob.core.domain.interest.repository.InterestRepository;
import com.bob.support.annotation.ContainerTest;

@DisplayName("관심사 등록 테스트")
@Transactional
@ContainerTest
record InterestRegisterTest(InterestRegister interestRegister, InterestRepository interestRepository) {

    @Test
    void 관심사_등록() {
        List<String> displayNames = List.of("Java", "SpRing");
        RegisterInterestsByNamesCommand command = RegisterInterestsByNamesCommand.of(displayNames);

        List<Interest> result = interestRegister.registerAll(command);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isNotNull();
        assertThat(result.get(0).getName()).isEqualTo("java");
        assertThat(result.get(1).getId()).isNotNull();
        assertThat(result.get(1).getName()).isEqualTo("spring");
    }

    @Test
    void 관심사_등록_시_이미_존재하면_기존_관심사를_반환한다() {
        Interest existingInterest = Interest.createInterest("java");
        interestRepository.save(existingInterest);

        RegisterInterestsByNamesCommand command = RegisterInterestsByNamesCommand.of(List.of("java", "spring"));
        List<Interest> result = interestRegister.registerAll(command);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(existingInterest.getId());
        assertThat(result.get(0).getName()).isEqualTo("java");
        assertThat(result.get(0).getId()).isNotNull();
        assertThat(result.get(1).getName()).isEqualTo("spring");
    }
}

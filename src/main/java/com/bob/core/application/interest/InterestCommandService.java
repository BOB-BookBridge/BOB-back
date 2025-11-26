package com.bob.core.application.interest;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.application.interest.dto.command.RegisterInterestsByNamesCommand;
import com.bob.core.application.interest.port.in.InterestRegister;
import com.bob.core.domain.interest.Interest;
import com.bob.core.domain.interest.repository.InterestRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class InterestCommandService implements InterestRegister {

    private final InterestRepository interestRepository;

    @Override
    public List<Interest> registerAll(RegisterInterestsByNamesCommand command) {
        List<String> normalizeNames = command.names().stream()
            .map(Interest::normalize)
            .distinct()
            .toList();

        interestRepository.saveAll(normalizeNames.stream()
            .filter(name -> interestRepository.findByName(name).isEmpty())
            .map(Interest::createInterest)
            .toList()
        );

        return normalizeNames.stream()
            .map(name -> interestRepository.findByName(name).orElseThrow())
            .toList();
    }
}

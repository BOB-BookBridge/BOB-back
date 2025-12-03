package com.bob.core.application.interest;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.application.interest.dto.query.FindInterestByNameQuery;
import com.bob.core.application.interest.port.in.InterestReader;
import com.bob.core.domain.interest.Interest;
import com.bob.core.domain.interest.repository.InterestRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InterestQueryService implements InterestReader {

    private final InterestRepository interestRepository;

    @Override
    public Interest read(Long id) {
        return interestRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("관심사를 찾을 수 없습니다. id : " + id));
    }

    @Override
    public Interest readByName(FindInterestByNameQuery query) {
        String name = Interest.normalize(query.name());
        return interestRepository.findByName(name)
            .orElseThrow(() -> new IllegalArgumentException("관심사를 찾을 수 없습니다. name : " + name));
    }
}

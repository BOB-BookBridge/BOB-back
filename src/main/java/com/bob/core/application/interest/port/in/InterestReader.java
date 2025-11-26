package com.bob.core.application.interest.port.in;

import com.bob.core.application.interest.dto.query.FindInterestByNameQuery;
import com.bob.core.domain.interest.Interest;

public interface InterestReader {

    Interest read(Long id);

    Interest readByName(FindInterestByNameQuery query);
}

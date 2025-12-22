package com.bob.core.interest.application.port.in;

import com.bob.core.interest.application.dto.query.FindInterestByNameQuery;
import com.bob.core.interest.domain.Interest;

public interface InterestReader {

    Interest read(Long id);

    Interest readByName(FindInterestByNameQuery query);
}

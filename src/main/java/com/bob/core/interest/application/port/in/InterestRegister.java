package com.bob.core.interest.application.port.in;

import java.util.List;

import com.bob.core.interest.application.dto.command.RegisterInterestsByNamesCommand;
import com.bob.core.interest.domain.Interest;

public interface InterestRegister {

    List<Interest> registerAll(RegisterInterestsByNamesCommand command);
}

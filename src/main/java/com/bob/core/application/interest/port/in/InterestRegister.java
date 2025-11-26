package com.bob.core.application.interest.port.in;

import java.util.List;

import com.bob.core.application.interest.dto.command.RegisterInterestsByNamesCommand;
import com.bob.core.domain.interest.Interest;

public interface InterestRegister {

    List<Interest> registerAll(RegisterInterestsByNamesCommand command);
}

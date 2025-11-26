package com.bob.integration.adapter.member;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.application.interest.dto.command.RegisterInterestsByNamesCommand;
import com.bob.core.application.interest.port.in.InterestRegister;
import com.bob.core.application.member.port.out.MemberInterestPort;
import com.bob.core.domain.interest.Interest;

@Component
@RequiredArgsConstructor
public class MemberInterestAdapter implements MemberInterestPort {

    private final InterestRegister interestRegister;

    @Override
    public List<Long> registerAll(List<String> displayNames) {
        RegisterInterestsByNamesCommand command = RegisterInterestsByNamesCommand.of(displayNames);

        List<Interest> interests = interestRegister.registerAll(command);

        return interests.stream().map(Interest::getId).toList();
    }
}

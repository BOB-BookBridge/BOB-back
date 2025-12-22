package com.bob.integration.adapter.member;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.interest.application.dto.command.RegisterInterestsByNamesCommand;
import com.bob.core.interest.application.port.in.InterestRegister;
import com.bob.core.interest.domain.Interest;
import com.bob.core.member.application.port.out.MemberInterestPort;

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

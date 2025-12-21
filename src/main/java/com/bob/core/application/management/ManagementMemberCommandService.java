package com.bob.core.application.management;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.bob.core.application.management.dto.command.ChangeManagementMemberStatusCommand;
import com.bob.core.application.management.port.in.ManagementMemberModifier;
import com.bob.core.application.management.port.out.ManagementMemberPort;
import com.bob.core.application.management.port.result.ManagementMember;

@Service
@Transactional
@RequiredArgsConstructor
public class ManagementMemberCommandService implements ManagementMemberModifier {

    private final ManagementMemberPort memberPort;

    @Override
    public ManagementMember changeStatus(ChangeManagementMemberStatusCommand command) {
        return memberPort.changeStatus(command.memberId(), command.status(), command.memo());
    }
}

package com.bob.admin.member.application;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.bob.admin.member.application.dto.command.ChangeManagementMemberStatusCommand;
import com.bob.admin.member.application.port.in.ManagementMemberModifier;
import com.bob.admin.member.application.port.out.ManagementMemberPort;
import com.bob.admin.member.application.port.result.ManagementMember;

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

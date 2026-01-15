package com.bob.core.member.adapter.out;

import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.admin.report.application.port.out.ManagementReportMemberPort;
import com.bob.core.member.application.dto.command.ChangeStatusCommand;
import com.bob.core.member.application.dto.command.UpdateMemoCommand;
import com.bob.core.member.application.port.in.MemberModifier;

@Component
@RequiredArgsConstructor
public class ManagementReportMemberAdapter implements ManagementReportMemberPort {

    private final MemberModifier memberModifier;

    @Override
    public void ban(UUID reportedId, String memo) {
        ChangeStatusCommand command = new ChangeStatusCommand("DEACTIVATED", memo);

        memberModifier.changeStatus(reportedId, command);
    }

    @Override
    public void updateMemo(UUID reportedId, String memo) {
        UpdateMemoCommand command = new UpdateMemoCommand(memo);

        memberModifier.updateMemoForAdmin(reportedId, command);
    }
}

package com.bob.core.member.adapter.api;

import static com.bob.core.shared.web.symbol.ResponseSymbol.CREATED;
import static com.bob.core.shared.web.symbol.ResponseSymbol.DELETED;
import static com.bob.core.shared.web.symbol.ResponseSymbol.SENT;
import static com.bob.core.shared.web.symbol.ResponseSymbol.UPDATED;

import java.util.UUID;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bob.core.member.adapter.api.request.ChangeMemberProfileImageRequest;
import com.bob.core.member.adapter.api.request.ChangePasswordRequest;
import com.bob.core.member.adapter.api.request.ChangeProfileRequest;
import com.bob.core.member.adapter.api.request.IssuePasswordRequest;
import com.bob.core.member.adapter.api.request.RecoverAccountRequest;
import com.bob.core.member.adapter.api.request.SignupRequest;
import com.bob.core.member.adapter.api.response.MemberDetailResponse;
import com.bob.core.member.application.dto.command.ChangePasswordCommand;
import com.bob.core.member.application.dto.command.ChangeProfileCommand;
import com.bob.core.member.application.dto.command.ChangeProfileImageCommand;
import com.bob.core.member.application.dto.command.CreateMemberCommand;
import com.bob.core.member.application.dto.result.MemberDetail;
import com.bob.core.member.application.port.in.MemberModifier;
import com.bob.core.member.application.port.in.MemberReader;
import com.bob.core.member.application.port.in.MemberRegister;
import com.bob.core.shared.web.AuthenticationId;
import com.bob.core.shared.web.CommonResponse;
import com.bob.core.shared.web.symbol.ResponseSymbol;
import com.bob.global.ratelimit.annotation.RateLimit;

@RequiredArgsConstructor
@RequestMapping("/members")
@RestController
public class MemberApi {

    private final MemberRegister memberRegister;
    private final MemberReader memberReader;
    private final MemberModifier memberModifier;

    @RateLimit(
        name = "signup",
        windowSecond = 60, maxRequest = 5
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<ResponseSymbol> signup(@Valid @RequestBody SignupRequest request) {
        CreateMemberCommand command = new CreateMemberCommand(
            request.email(), request.password(), request.nickname(), request.emdId()
        );

        memberRegister.signup(command);

        return new CommonResponse<>(true, CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<MemberDetailResponse> readDetail(@AuthenticationId UUID memberId) {
        MemberDetail detail = memberReader.readDetail(memberId, true);

        return ResponseEntity.ok(MemberDetailResponse.of(detail));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberDetailResponse> readOtherDetail(@PathVariable UUID memberId) {
        MemberDetail detail = memberReader.readDetail(memberId, false);

        return ResponseEntity.ok(MemberDetailResponse.of(detail));
    }

    @PatchMapping("/me")
    public CommonResponse<ResponseSymbol> changeProfile(
        @Valid @RequestBody ChangeProfileRequest request,
        @AuthenticationId UUID memberId
    ) {
        ChangeProfileCommand command = new ChangeProfileCommand(
            request.nickname(), request.emdId(), request.areaAuthenticate(), request.lat(), request.lon(),
            request.interests()
        );

        memberModifier.changeProfile(memberId, command);

        return new CommonResponse<>(true, UPDATED);
    }

    @RateLimit(
        name = "change-password",
        windowSecond = 60, maxRequest = 1,
        target = RateLimit.LimitTarget.MEMBER_ID,
        value = "#memberId"
    )
    @PatchMapping("/me/password")
    public CommonResponse<ResponseSymbol> changePassword(
        @Valid @RequestBody ChangePasswordRequest request,
        @AuthenticationId UUID memberId
    ) {
        ChangePasswordCommand command = new ChangePasswordCommand(request.oldPassword(), request.newPassword());

        memberModifier.changePassword(memberId, command);

        return new CommonResponse<>(true, UPDATED);
    }

    @RateLimit(
        name = "issue-temp-password",
        windowSecond = 60, maxRequest = 1
    )
    @PatchMapping("/temp/password")
    public CommonResponse<ResponseSymbol> sendTempPassword(@Valid @RequestBody IssuePasswordRequest request) {
        memberModifier.issueTempPassword(request.email());

        return new CommonResponse<>(true, SENT);
    }

    @PatchMapping("/me/image")
    public CommonResponse<ResponseSymbol> changeProfileImage(
        @Valid @RequestBody ChangeMemberProfileImageRequest request,
        @AuthenticationId UUID memberId
    ) {
        ChangeProfileImageCommand command = new ChangeProfileImageCommand(request.fileName());

        memberModifier.changeProfileImage(memberId, command);

        return new CommonResponse<>(true, UPDATED);
    }

    @PatchMapping("/recover")
    public CommonResponse<ResponseSymbol> activateAccount(
        @Valid @RequestBody RecoverAccountRequest request
    ) {
        memberModifier.activate(request.email());

        return new CommonResponse<>(true, UPDATED);
    }

    @DeleteMapping("/me")
    public CommonResponse<ResponseSymbol> deactivateMember(
        HttpServletResponse response,
        @AuthenticationId UUID memberId
    ) {
        memberModifier.deactivate(memberId, response);

        return new CommonResponse<>(true, DELETED);
    }
}

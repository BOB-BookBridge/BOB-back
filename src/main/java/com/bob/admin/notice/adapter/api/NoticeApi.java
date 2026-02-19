package com.bob.admin.notice.adapter.api;

import static com.bob.shared.web.response.ResponseSymbol.CREATED;

import java.util.UUID;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bob.admin.notice.adapter.api.request.RegisterBannerRequest;
import com.bob.admin.notice.application.dto.command.RegisterBannerCommand;
import com.bob.admin.notice.application.port.in.NoticeRegister;
import com.bob.shared.web.annotation.AuthenticationId;
import com.bob.shared.web.response.CommonResponse;
import com.bob.shared.web.response.ResponseSymbol;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeApi {

    private final NoticeRegister noticeRegister;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/banner")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<ResponseSymbol> registerBanner(
        @Valid @RequestBody RegisterBannerRequest request,
        @AuthenticationId UUID memberId
    ) {
        var command = new RegisterBannerCommand(memberId, request.content(), request.endTime());

        noticeRegister.registerBanner(command);

        return new CommonResponse<>(true, CREATED);
    }
}

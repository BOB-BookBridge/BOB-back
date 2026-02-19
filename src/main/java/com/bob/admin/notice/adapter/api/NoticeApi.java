package com.bob.admin.notice.adapter.api;

import static com.bob.shared.web.response.ResponseSymbol.CREATED;
import static com.bob.shared.web.response.ResponseSymbol.UPDATED;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bob.admin.notice.adapter.api.request.RegisterAlertRequest;
import com.bob.admin.notice.adapter.api.request.RegisterBannerRequest;
import com.bob.admin.notice.adapter.api.response.AlertNoticesResponse;
import com.bob.admin.notice.adapter.api.response.BannerNoticeResponse;
import com.bob.admin.notice.adapter.api.response.NoticeDetailResponse;
import com.bob.admin.notice.application.dto.command.RegisterAlertCommand;
import com.bob.admin.notice.application.dto.command.RegisterBannerCommand;
import com.bob.admin.notice.application.port.in.NoticeModifier;
import com.bob.admin.notice.application.port.in.NoticeReader;
import com.bob.admin.notice.application.port.in.NoticeRegister;
import com.bob.admin.notice.application.port.result.NoticeDetail;
import com.bob.shared.web.annotation.AuthenticationId;
import com.bob.shared.web.response.CommonResponse;
import com.bob.shared.web.response.ResponseSymbol;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeApi {

    private final NoticeRegister noticeRegister;
    private final NoticeReader noticeReader;
    private final NoticeModifier noticeModifier;

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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/alerts")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<ResponseSymbol> registerAlert(
        @Valid @RequestBody RegisterAlertRequest request,
        @AuthenticationId UUID memberId
    ) {
        var command = new RegisterAlertCommand(memberId, request.title(), request.content());

        noticeRegister.registerAlert(command);

        return new CommonResponse<>(true, CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/alerts")
    public List<AlertNoticesResponse> readRecentAlerts() {
        return noticeReader.readCurrentAlertNotices().stream()
            .map(AlertNoticesResponse::of)
            .toList();
    }

    @GetMapping("/banner")
    public ResponseEntity<BannerNoticeResponse> readBanner() {
        return noticeReader.readBanner()
            .map(BannerNoticeResponse::of)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{noticeId}")
    public NoticeDetailResponse readAlerts(@PathVariable Long noticeId) {
        NoticeDetail detail = noticeReader.readDetail(noticeId);

        return NoticeDetailResponse.of(detail);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/banner")
    public CommonResponse<ResponseSymbol> deactivateCurrentBanner() {
        noticeModifier.deactivateCurrentBanner();

        return new CommonResponse<>(true, UPDATED);
    }
}

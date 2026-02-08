package com.bob.admin.filter.adapter.api;

import static com.bob.shared.web.response.ResponseSymbol.CREATED;
import static com.bob.shared.web.response.ResponseSymbol.DELETED;

import java.util.List;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bob.admin.filter.adapter.api.request.CreateFilterWordRequest;
import com.bob.admin.filter.application.dto.command.CreateFilterWordCommand;
import com.bob.admin.filter.application.port.in.ManagementFilterWordReader;
import com.bob.admin.filter.application.port.in.ManagementFilterWordRegister;
import com.bob.admin.filter.application.port.in.ManagementFilterWordRemover;
import com.bob.admin.filter.domain.ManagementFilterWord;
import com.bob.shared.web.response.CommonResponse;
import com.bob.shared.web.response.ResponseSymbol;

@RestController
@RequestMapping("/management/filter-words")
@RequiredArgsConstructor
public class ManagementFilterWordApi {

    private final ManagementFilterWordRegister filterWordRegister;
    private final ManagementFilterWordReader filterWordReader;
    private final ManagementFilterWordRemover filterWordRemover;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ManagementFilterWord> readAll() {
        return filterWordReader.readAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<ResponseSymbol> create(@Valid @RequestBody CreateFilterWordRequest request) {
        CreateFilterWordCommand command = new CreateFilterWordCommand(request.word());

        filterWordRegister.register(command);

        return new CommonResponse<>(true, CREATED);
    }

    @DeleteMapping("/{wordId}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<ResponseSymbol> delete(@PathVariable Long wordId) {
        filterWordRemover.remove(wordId);

        return new CommonResponse<>(true, DELETED);
    }
}

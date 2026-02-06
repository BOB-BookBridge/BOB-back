package com.bob.admin.filter.application;

import static com.bob.global.exception.response.ApplicationError.FILTER_WORD_ALREADY_EXISTS;
import static com.bob.global.exception.response.ApplicationError.FILTER_WORD_NOT_EDITABLE;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.admin.filter.application.dto.command.CreateFilterWordCommand;
import com.bob.admin.filter.application.port.in.ManagementFilterWordReader;
import com.bob.admin.filter.application.port.in.ManagementFilterWordRegister;
import com.bob.admin.filter.application.port.in.ManagementFilterWordRemover;
import com.bob.admin.filter.application.port.out.FilterWordManager;
import com.bob.admin.filter.domain.ManagementFilterWord;
import com.bob.global.exception.exceptions.ApplicationException;

@Service
@Transactional
@RequiredArgsConstructor
public class ManagementFilterWordCommandService implements ManagementFilterWordRegister, ManagementFilterWordRemover {

    private final FilterWordManager filterWordManager;
    private final ManagementFilterWordReader managementFilterWordReader;

    @Override
    public ManagementFilterWord register(CreateFilterWordCommand command) {
        if (filterWordManager.existsByWord(command.word()))
            throw new ApplicationException(FILTER_WORD_ALREADY_EXISTS);

        ManagementFilterWord filterWord = ManagementFilterWord.create(command.word());

        return filterWordManager.save(filterWord);
    }

    @Override
    public void remove(Long wordId) {
        ManagementFilterWord filterWord = managementFilterWordReader.read(wordId);

        if (!filterWord.isEditable())
            throw new ApplicationException(FILTER_WORD_NOT_EDITABLE);

        filterWordManager.remove(filterWord);
    }
}

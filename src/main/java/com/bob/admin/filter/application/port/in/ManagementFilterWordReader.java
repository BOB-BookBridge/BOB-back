package com.bob.admin.filter.application.port.in;

import java.util.List;

import com.bob.admin.filter.domain.ManagementFilterWord;

public interface ManagementFilterWordReader {

    List<ManagementFilterWord> readAll();

    ManagementFilterWord read(Long wordId);
}

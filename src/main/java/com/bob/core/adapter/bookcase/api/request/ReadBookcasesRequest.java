package com.bob.core.adapter.bookcase.api.request;

import java.util.List;

public record ReadBookcasesRequest(String key, List<Long> require) {

}

package com.bob.core.bookcase.adapter.api.request;

import java.util.List;

public record ReadBookcasesRequest(String key, List<Long> require) {

}

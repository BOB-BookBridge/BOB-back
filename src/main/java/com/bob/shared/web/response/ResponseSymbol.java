package com.bob.shared.web.response;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ResponseSymbol {
    OK, FAILED, CREATED, UPDATED, DELETED, SENT, VERIFIED;
}


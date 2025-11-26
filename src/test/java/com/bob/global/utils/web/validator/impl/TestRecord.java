package com.bob.global.utils.web.validator.impl;

import com.bob.global.utils.web.validator.AtLeastOneNotNull;

@AtLeastOneNotNull(anyOf = {"name", "age"})
public record TestRecord(String name, String nickname, Integer age) {

}

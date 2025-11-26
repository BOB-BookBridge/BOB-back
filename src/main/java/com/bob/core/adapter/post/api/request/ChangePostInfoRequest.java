package com.bob.core.adapter.post.api.request;

import com.bob.global.utils.web.validator.AtLeastOneNotNull;

@AtLeastOneNotNull(anyOf = {"bookStatus", "description", "wishOnly"})
public record ChangePostInfoRequest(String bookStatus, String description, Boolean wishOnly) {

}

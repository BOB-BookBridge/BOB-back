package com.bob.core.post.adapter.api.request;

import com.bob.shared.web.annotation.AtLeastOneNotNull;

@AtLeastOneNotNull(anyOf = {"bookStatus", "description", "wishOnly"})
public record ChangePostInfoRequest(String bookStatus, String description, Boolean wishOnly) {

}

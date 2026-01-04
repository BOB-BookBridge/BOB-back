package com.bob.core.post.domain;

import static java.util.Objects.requireNonNull;

import java.util.UUID;

import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.bob.shared.entity.AbstractEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostFavorite extends AbstractEntity {

    private UUID memberId;

    public static PostFavorite createPostFavorite(UUID memberId) {
        return PostFavorite.builder()
            .memberId(requireNonNull(memberId))
            .build();
    }
}

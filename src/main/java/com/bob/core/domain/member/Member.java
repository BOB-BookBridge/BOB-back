package com.bob.core.domain.member;

import static com.bob.core.domain.member.MemberInterest.createMemberInterest;
import static com.bob.core.domain.member.MemberWish.createMemberWish;
import static com.bob.core.domain.member.Status.ACTIVE;
import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.bob.global.utils.uuid.GeneratedUuidV7;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member {

    @Id
    @GeneratedUuidV7
    private UUID id;

    private SocialProvider provider;

    private Status status;

    private Role role;

    private String email;
    private String password;
    private String nickname;
    private String profileImageUrl;

    private MemberArea area;

    @Builder.Default
    private List<MemberInterest> interests = new ArrayList<>();

    @Builder.Default
    private List<MemberWish> wishes = new ArrayList<>();

    private String memo;

    private LocalDateTime lastActiveAt;
    private LocalDateTime createdAt;

    public static Member createMember(String email, String password, String nickname, Integer emdAreaId) {
        return Member.builder()
            .status(ACTIVE)
            .role(Role.USER)
            .email(requireNonNull(email))
            .password(requireNonNull(password))
            .nickname(requireNonNull(nickname))
            .area(MemberArea.createArea(emdAreaId))
            .createdAt(LocalDateTime.now())
            .build();
    }

    public static Member createSocialMember(String email, String provider, String nickname) {
        return Member.builder()
            .provider(SocialProvider.valueOf(requireNonNull(provider)))
            .status(ACTIVE)
            .role(Role.USER)
            .email(requireNonNull(email))
            .password(null)
            .nickname(requireNonNull(nickname))
            .area(MemberArea.createNonAuthenticateArea(213))
            .createdAt(LocalDateTime.now())
            .build();
    }

    public void updateInfo(String nickname, Integer emdId, List<Long> interestIds, List<String> interestNames) {
        this.nickname = nickname;

        this.area.updateAuthentication(emdId);

        this.interests.clear();
        for (int i = 0; i < interestIds.size(); i++) {
            MemberInterest interest = createMemberInterest(interestIds.get(i), interestNames.get(i));
            this.interests.add(interest);
        }
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateProfileImageUrl(String newProfileImageUrl) {
        profileImageUrl = newProfileImageUrl;
    }

    public void addWish(Long bookId) {
        MemberWish wish = createMemberWish(bookId);

        this.wishes.add(wish);
    }

    public void removeWish(Long wishId) {
        this.wishes.removeIf(wish -> Objects.equals(wish.getId(), wishId));
    }

    public void activate() {
        this.status = Status.ACTIVE;
    }

    public void deactivate() {
        this.status = Status.DEACTIVATED;
    }

    public void ban() {
        this.status = Status.BANNED;
    }

    public void updateLastActiveTime() {
        this.lastActiveAt = LocalDateTime.now();
    }

    public void updateMemo(String memo) {
        this.memo = memo;
    }

    public boolean isDeactivated() {
        return this.status == Status.DEACTIVATED;
    }

    public boolean isBanned() {
        return this.status == Status.BANNED;
    }
}

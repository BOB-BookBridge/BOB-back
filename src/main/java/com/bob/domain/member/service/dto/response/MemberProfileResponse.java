package com.bob.domain.member.service.dto.response;

import static com.bob.domain.member.entity.Status.WITHDRAW;

import com.bob.domain.member.entity.Member;
import com.bob.domain.member.service.dto.response.internal.MemberBookSummary;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record MemberProfileResponse(
    boolean isSocial,
    UUID memberId,
    String email,
    String nickname,
    String profileImageUrl,
    List<String> interests,
    Area area,
    List<MemberBookSummary> books
) {

  public static MemberProfileResponse from(
      Member member,
      List<String> interestNames,
      MemberAreaSummaryResponse areaSummary,
      List<MemberBookSummary> books
  ) {
    final boolean removed = member.getStatus() == WITHDRAW;
    final String nickname = removed ? "(알 수 없음)" : member.getNickname();
    final String email = removed ? "delete" : member.getEmail();
    final String profileImageUrl = removed ? null : member.getProfileImageUrl();
    final List<String> interests = removed ? List.of() : interestNames;

    return MemberProfileResponse.builder()
        .isSocial(member.getProvider() != null)
        .memberId(member.getId())
        .email(email)
        .nickname(nickname)
        .profileImageUrl(profileImageUrl)
        .interests(interests)
        .area(Area.of(areaSummary.emdId(), areaSummary.validity(), areaSummary.authenticatedAt()))
        .books(books)
        .build();
  }

  @Builder
  public record Area(
      int emdId,
      boolean isAuthentication,
      LocalDate authenticatedAt
  ) {

    public static Area of(int emdId, boolean isAuthentication, LocalDate authenticatedAt) {
      return Area.builder()
          .emdId(emdId)
          .isAuthentication(isAuthentication)
          .authenticatedAt(authenticatedAt)
          .build();
    }
  }
}



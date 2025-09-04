package com.bob.domain.member.entity;

import com.bob.global.audit.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "member_interests")
public class MemberInterest extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private UUID memberId;

  @Column(nullable = false)
  private Long interestId;

  @Column(nullable = false, length = 100)
  private String displayName;

  public static MemberInterest of(UUID memberId, Long interestId, String displayName) {
    return MemberInterest.builder()
        .memberId(memberId)
        .interestId(interestId)
        .displayName(displayName)
        .build();
  }
}

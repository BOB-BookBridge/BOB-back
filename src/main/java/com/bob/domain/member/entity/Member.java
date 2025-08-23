package com.bob.domain.member.entity;

import com.bob.global.audit.BaseTime;
import com.bob.global.utils.uuid.GeneratedUuidV7;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
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
@Table(name = "members")
public class Member extends BaseTime {

  @Id
  @GeneratedUuidV7
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column
  private SocialProvider provider;

  @Column(unique = true, length = 50, nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(length = 20, nullable = false)
  private String nickname;

  @Column
  private String profileImageUrl;

  @Column
  private boolean isRemove;

  public void updatePassword(String newPassword) {
    password = newPassword;
  }

  public void updateNickname(String newNickname) {
    nickname = newNickname;
  }

  public void updateProfileImageUrl(String newProfileImageUrl) {
    profileImageUrl = newProfileImageUrl;
  }

  public void updateRemoveStatus(boolean isRemove) {
    this.isRemove = isRemove;
  }

  public boolean isEqualsNickname(String oldNickname) {
    return Objects.equals(nickname, oldNickname);
  }
}

package com.bob.domain.area.entity.activity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
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
@Table(name = "activity_areas")
public class ActivityArea {

  @EmbeddedId
  private ActivityAreaId id;

  @Column(nullable = false)
  private LocalDate authenticationAt;

  public static ActivityArea create(ActivityAreaId id) {
    return new ActivityArea(id, LocalDate.now());
  }

  public static ActivityAreaId createId(UUID memberId, Integer emdAreaId) {
    return new ActivityAreaId(memberId, emdAreaId);
  }

  public void updateAuthenticationAt(LocalDate newDate) {
    authenticationAt = newDate;
  }

  public boolean isValidAuthentication() {
    return !authenticationAt.isBefore(LocalDate.now().minusMonths(1));
  }
}

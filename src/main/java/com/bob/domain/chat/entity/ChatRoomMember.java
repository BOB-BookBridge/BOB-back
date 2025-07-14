package com.bob.domain.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@Table(name = "chat_room_members")
public class ChatRoomMember {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long chatRoomId;

  @Column(nullable = false)
  private UUID memberId;

  @Column(nullable = false)
  private LocalDateTime enteredAt;

  private LocalDateTime exitedAt;

  public void updateExitedAt(LocalDateTime currentTime) {
    this.exitedAt = currentTime;
  }

  public void reEnterChatRoom(LocalDateTime now) {
    this.enteredAt = now;
    this.exitedAt = null;
  }
}

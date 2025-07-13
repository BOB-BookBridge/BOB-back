package com.bob.domain.chat.entity;

import com.bob.global.audit.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@Table(name = "chat_rooms")
public class ChatRoom extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long postId;

  @Column(nullable = false)
  private Long tradeId;

  @Column(length = 100, nullable = false)
  private String titleSuffix;

  @Column
  private String lastChatMessage;

  @Column
  private LocalDateTime lastChatAt;

  @Column(nullable = false)
  @Builder.Default
  private Boolean enableStatus = false;

  public void updateChatRoomStatus(boolean status) {
    this.enableStatus = status;
  }

  public void updateChatRoomLastMessageInfo(String lastChatMessage, LocalDateTime lastChatAt) {
    if (lastChatMessage == null || lastChatMessage.isEmpty() || lastChatMessage.isBlank()) {
      lastChatMessage = "사진";
    }
    this.lastChatMessage = lastChatMessage;
    this.lastChatAt = lastChatAt;
  }
}
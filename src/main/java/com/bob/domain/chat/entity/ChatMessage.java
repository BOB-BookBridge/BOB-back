package com.bob.domain.chat.entity;

import com.bob.domain.chat.entity.type.ChatMessageType;
import com.bob.global.audit.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "chat_messages")
public class ChatMessage extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long chatRoomId;

  @Column(nullable = false)
  private UUID senderId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ChatMessageType chatMessageType;

  @Column(length = 500)
  private String chatMessage;

  @Column
  private String chatImageUrl;

  @Column(nullable = false)
  @Builder.Default
  private Boolean isRead = false;

  public void readMessage() {
    this.isRead = true;
  }
}

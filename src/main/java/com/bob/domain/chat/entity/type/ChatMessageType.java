package com.bob.domain.chat.entity.type;

public enum ChatMessageType {
  TEXT, IMAGE, MIX, SYSTEM;

  public boolean hasFile() {
    return this == IMAGE || this == MIX;
  }
}

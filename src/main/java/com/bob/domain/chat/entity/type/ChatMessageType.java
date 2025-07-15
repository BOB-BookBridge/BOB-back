package com.bob.domain.chat.entity.type;

public enum ChatMessageType {
  MESSAGE, IMAGE, MIX, SYSTEM;

  public boolean hasFile() {
    return this == IMAGE || this == MIX;
  }
}

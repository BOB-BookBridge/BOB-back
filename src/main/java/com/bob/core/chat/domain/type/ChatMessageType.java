package com.bob.core.chat.domain.type;

public enum ChatMessageType {
    TEXT, IMAGE, MIX, SYSTEM;

    public boolean hasFile() {
        return this == IMAGE || this == MIX;
    }
}

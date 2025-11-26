package com.bob.core.domain.chat.type;

public enum ChatMessageType {
    TEXT, IMAGE, MIX, SYSTEM;

    public boolean hasFile() {
        return this == IMAGE || this == MIX;
    }
}

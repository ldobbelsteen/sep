package org.lukos.model.chatsystem;

public record ChatStatus(int id, ChatType type, boolean isOpen) {
    @Override
    public boolean equals(Object o) {
        if (o instanceof ChatStatus chatStatus) {
            return id == (chatStatus.id()) && type == (chatStatus.type()) && isOpen == chatStatus.isOpen;
        }
        return false;
    }
}

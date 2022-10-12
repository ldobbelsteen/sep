package org.lukos.model.chatsystem;

/**
 * Record to store chatID and its corresponding type together
 *
 * @author Marco Pleket (1295713)
 * @since 28-03-2022
 */
public record ChatIdentifier(int id, ChatType type) {
    @Override
    public boolean equals(Object o) {
        if (o instanceof ChatIdentifier chatIdentifier) {
            return id == (chatIdentifier.id()) && type == (chatIdentifier.type());
        }
        return false;
    }
}

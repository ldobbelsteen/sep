package org.lukos.model.chatsystem;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * MessageEntry for the ChatMessage datatype
 *
 * @author Marco Pleket (1295713)
 * @since 22-03-22
 */
public record MessageEntry(int id, Instant timestamp, String content) {
}

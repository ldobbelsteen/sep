package org.lukos.controller.websocket;

/**
 * The datatype for a WebSocket notification of joining, leaving, starting or canceling a game
 *
 * @author Marco Pleket (1295713)
 * @since 02-04-2022
 */
public record InstanceNotification(int uid, InstanceAction action) {
}

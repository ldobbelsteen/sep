package org.lukos.model.chatsystem;

/**
 * ChatMessage datatype used for the WebSocket
 *
 * @author Marco Pleket (1295713)
 * @since 22-03-22
 */
public class ChatMessage {
    public int chatId;
    public MessageEntry message;

    public ChatMessage(int chatId, MessageEntry message) {
        this.chatId = chatId;
        this.message = message;
    }
}

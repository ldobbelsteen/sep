package org.lukos.controller.response;

import org.lukos.model.chatsystem.ChatMessage;

import java.util.List;

/**
 * The response datatype that sends a list of chat messages.
 *
 * @author Marco Pleket (1295713)
 * @since 29-03-2022
 */
public class MessageResponse extends SuccessResponse {
    /** The list of chat messages of the response. */
    private final List<ChatMessage> messages;

    /**
     * Constructor for responses of {@code MessageResponse}.
     *
     * @param messages the messages of the response
     */
    public MessageResponse(List<ChatMessage> messages) {
        super(null); // TODO: add message

        this.messages = messages;
    }

    /**
     * Returns a list of obtained messages.
     *
     * @return the list of messages.
     */
    public List<ChatMessage> getHistory() {
        return messages;
    }
}

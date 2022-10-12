package org.lukos.controller.response;

import org.lukos.model.chatsystem.ChatStatus;

import java.util.List;

/**
 * The response datatype that sends a list of chats for the specified player.
 *
 * @author Marco Pleket (1295713)
 * @since 29-03-2022
 */
public class ChatListResponse extends SuccessResponse {
    /** The list of chats and their types for the response. */
    private final List<ChatStatus> chats;

    /**
     * Constructor for responses of {@code ChatListResponse}.
     *
     * @param chats the chats of the response
     */
    public ChatListResponse(List<ChatStatus> chats) {
        super(null); // TODO: add message

        this.chats = chats;
    }

    /**
     * Returns a list of obtained chats.
     *
     * @return the list of chats.
     */
    public List<ChatStatus> getChats() {
        return chats;
    }
}

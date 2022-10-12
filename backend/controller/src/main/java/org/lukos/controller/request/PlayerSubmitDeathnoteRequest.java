package org.lukos.controller.request;

/**
 * The request datatype for a player submitting the new content for their deathnote.
 *
 * @author Rick van der Heijden (1461923)
 * @since 28-03-2022
 */
public class PlayerSubmitDeathnoteRequest {
    /** The new content for the deathnote. */
    private String content;

    /**
     * Returns the new content of the deathnote.
     *
     * @return the new content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the new content of the deathnote.
     *
     * @param content the new content
     */
    public void setContent(String content) {
        this.content = content;
    }
}

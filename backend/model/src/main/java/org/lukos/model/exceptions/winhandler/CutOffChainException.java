package org.lukos.model.exceptions.winhandler;

/**
 * This exception is to report that someone tries to cut off a part of the {@code WinHandler} chain.
 *
 * @author Rick van der Heijden (1461923)
 * @since 06-03-2022
 */
public class CutOffChainException extends Exception {

    /**
     * Constructs a {@code CutOffChainException}.
     */
    public CutOffChainException() { }

    /**
     * Constructs a {@code CutOffChainException} with a {@code message}.
     *
     * @param message the message
     */
    public CutOffChainException(String message) {
        super(message);
    }
}

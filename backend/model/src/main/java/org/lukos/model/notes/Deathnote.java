package org.lukos.model.notes;

import org.lukos.model.exceptions.notes.ContentTooLongException;
import org.lukos.model.exceptions.notes.ModificationNotAllowedException;
import org.lukos.model.exceptions.notes.NoteException;

/**
 * Deathnote class implements iNote interface.
 * It contains information about the deathnote of one player
 *
 * @author Valentijn van den Berg (1457446)
 * @since 01-03-2022
 */
public class Deathnote implements INote {

    /** Maximum length of the content */
    private static final int MAX_LENGTH = 1000;

    /** String containing the content of the deathnote */
    private String content;

    /** Whether this note is changeable */
    private boolean isChangeable;

    /**
     * Constructor
     */
    public Deathnote() {
        this.content = "";
        this.isChangeable = true;
    }

    /**
     * Constructor with pre-defined values.
     */
    public Deathnote(String content, boolean isChangeable) {
        this.content = content;
        this.isChangeable = isChangeable;
    }

    @Override
    public void setContent(String newContent) throws NoteException {
        // Check if this note can be changed
        if (!isChangeable) {
            throw new ModificationNotAllowedException("This deathnote currently cannot be changed!");
        }
        // Check if the length of the new content exceeds the character limit
        if (newContent.length() > MAX_LENGTH) {
            throw new ContentTooLongException("The new content of this deathnote is too long!");
        }
        // We are allowed to set the new content
        this.content = newContent;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setChangeable(boolean isChangeable) {
        this.isChangeable = isChangeable;
    }

    @Override
    public boolean getChangeable() {
        return isChangeable;
    }
}

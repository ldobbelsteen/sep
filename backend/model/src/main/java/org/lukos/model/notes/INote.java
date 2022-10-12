package org.lukos.model.notes;

import org.lukos.model.exceptions.notes.ContentTooLongException;
import org.lukos.model.exceptions.notes.ModificationNotAllowedException;
import org.lukos.model.exceptions.notes.NoteException;

/**
 * iNote interface.
 * Used for the notes system (e.g. deathnotes)
 *
 * @author Valentijn van den Berg (1457446)
 * @since 01-03-2022
 */
public interface INote {

    /**
     * Set the notes new content
     *
     * @param newContent the new content
     * @throws ModificationNotAllowedException if the method is called when change is not allowed
     * @throws ContentTooLongException if the argument contains too many characters (default: 1000)
     */
    void setContent(String newContent) throws NoteException;

    /**
     * Get the content of the note
     *
     * @return the content of the note
     */
    String getContent();

    /**
     * Set whether the note is changeable
     *
     * @param isChangeable the new value
     */
    void setChangeable(boolean isChangeable);

    /**
     * Get whether the note is changeable
     *
     * @return whether the note is changeable
     */
    boolean getChangeable();

}

package org.lukos.model.notes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.model.exceptions.notes.ContentTooLongException;
import org.lukos.model.exceptions.notes.ModificationNotAllowedException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Deathnote class
 *
 * @author Valentijn van den Berg (1457446)
 * @since 03-03-2022
 */
public class DeathnoteTest {

    /** max length of the content */
    private final int maxLength = 1000;
    private Deathnote dn;

    @BeforeEach
    public void beforeEachTest() {
        dn = new Deathnote();
    }

    /**
     * Basic test, test constructor
     *
     * @utp.description Tests whether the constructor with no parameters initializes the content and changable boolean
     * correctly.
     */
    @Test
    public void constructorTest1() {
        assertEquals("", dn.getContent(), "Deathnote should be empty at creation!");
        assertTrue(dn.getChangeable(), "Deathnote should be changeable at creation!");
    }

    /**
     * @utp.description Tests whether the constructor with parameters initializes the content and changable boolean
     * correctly.
     */
    @Test
    public void constructorTest2() {
        String testText = "This is a testing content";
        Deathnote deathnote = new Deathnote(testText, true);
        assertEquals(testText, deathnote.getContent(), "Deathnote should not be empty!");
        assertTrue(deathnote.getChangeable(), "Deathnote should be changeable!");

        String testText2 = "This is a testing content, for testing the second time.";
        Deathnote deathnote2 = new Deathnote(testText2, false);
        assertEquals(testText2, deathnote2.getContent(), "Deathnote should have content!");
        assertFalse(deathnote2.getChangeable(), "Deathnote should not be changeable!");
    }

    /**
     * Try to edit content when changeable is true.
     *
     * @utp.description Tests whether content can be edited when {@code changable == true} for that {@code Deathnote}.
     * @utp.test_items {@code Deathnote}
     * @utp.input_specs -
     * @utp.output_specs Whether changing the content of a {@code Deathnote} is possible when {@code changable == true}
     * for that {@code Deathnote}.
     * @utp.env_needs -
     */
    @Test
    public void editContentTest1() {
        // Set new content
        String newContent = "New content";
        try {
            dn.setContent(newContent);
        } catch (Exception e) {
            fail("An exception was thrown!");
        }

        // Check if the content has been set correctly
        assertEquals(newContent, dn.getContent(), "The content is not the same!");
    }

    /**
     * When changeable is false, an exception should be thrown when editing the content.
     *
     * @utp.description Tests whether an {@code Exception} is thrown when changing {@code Deathnote} contents when
     * {@code changable == false}.
     * @utp.test_items {@code Deathnote}
     * @utp.input_specs -
     * @utp.output_specs Whether an {@code Exception} is thrown when changing the contents of a {@code Deathnote} when
     * {@code changable == false} for that {@code Deathnote}.
     * @utp.env_needs -
     */
    @Test
    public void editContentTest2() {
        // Set isChangeable to false and check if it was successful
        dn.setChangeable(false);
        assertFalse(dn.getChangeable(), "Changeable should be false!");

        // Try to change the content
        try {
            String newContent = "New content";
            dn.setContent(newContent);

            fail("Should have thrown an exception!");
        } catch (Exception e) {

            Class<?> expected = ModificationNotAllowedException.class;
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null.");

        }
    }

    /**
     * Try to edit content when changeable is true, false and true again.
     *
     * @utp.description Tests whether changing the contents of a {@code Deathnote} is correctly handled when {@code
     * changable == true}, {@code changable == false} and {@code changable == true} in succession.
     * @utp.test_items {@code Deathnote}
     * @utp.input_specs -
     * @utp.output_specs Whether changing the contents of a {@code Deathnote} succeeds when {@code changable == true},
     * then fails when {@code changable == false} and then succeeds again when {@code changable == true} is set once
     * again.
     * @utp.env_needs -
     */
    @Test
    public void editContentTest3() {
        // -- Change the content when isChangeable is true --

        // Set new content
        String newContent = "New content";
        try {
            dn.setContent(newContent);
        } catch (Exception e) {
            fail("An exception was thrown during the first change!");
        }

        // Check if the content has been set correctly
        assertEquals(newContent, dn.getContent(), "Content check 1: The content is not the same!");

        // -- Change the content when isChangeable is false --

        // Set isChangeable to false and check if it was successful
        dn.setChangeable(false);
        assertFalse(dn.getChangeable(), "Changeable should be false!");
        newContent = "second content";

        // Try to change the content
        try {
            dn.setContent(newContent);

            fail("Should have thrown an exception during second change!");
        } catch (Exception e) {

            Class<?> expected = ModificationNotAllowedException.class;
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null.");

        }
        assertEquals("New content", dn.getContent(), "Content check 2: The content should still be 'New content'!");

        // -- Change the content when isChangeable is true again --

        // Set isChangeable to true and check if it was successful
        dn.setChangeable(true);
        assertTrue(dn.getChangeable(), "Changeable should be true!");

        // Set new content
        newContent = "Other content";
        try {
            dn.setContent(newContent);
        } catch (Exception e) {
            fail("An exception was thrown during the third change!");
        }

        // Check if the content has been set correctly
        assertEquals(newContent, dn.getContent(), "Content check 3: The content is not the same!");
    }

    /**
     * Try to edit content when the new content is exactly {@code maxLength} chars, DEFAULT 1000.
     *
     * @utp.description Tests whether the contents of a {@code Deathnote} can be changed to a string of the maximum
     * length.
     * @utp.test_items {@code Deathnote}
     * @utp.input_specs -
     * @utp.output_specs Whether the contents of a {@code Deathnote} can be changed to a string of length {@code
     * maxLength}.
     * @utp.env_needs -
     */
    @Test
    public void editContentTooLongTest1() {
        // Create string of max characters
        String newContent = "x".repeat(maxLength);

        // Make sure the new content has max characters
        assertEquals(maxLength, newContent.length(), "the message is not 1000 characters");

        // Set new content
        try {
            dn.setContent(newContent);
        } catch (Exception e) {
            fail("An exception was thrown!");
        }

        // Check if the content has been set correctly
        assertEquals(newContent, dn.getContent(), "The content is not the same!");
    }

    /**
     * Try to edit content when the new content is maxLength + 1 chars, DEFAULT 1001; an {@code Exception} should be
     * thrown.
     *
     * @utp.description Tests whether an {@code Exception} is thrown when submitting contents for a {@code Deathnote}
     * that are slightly too large.
     * @utp.test_items {@code Deathnote}
     * @utp.input_specs -
     * @utp.output_specs Whether an {@code ContentTooLongException} is thrown when attempting to change the contents of
     * a {@code Deathnote} to a string of length {@code maxLength + 1}.
     * @utp.env_needs -
     */
    @Test
    public void editContentTooLongTest2() {
        // Create string of max characters + 1
        String newContent = "x".repeat(maxLength + 1);

        // Make sure the new content has max characters + 1
        assertEquals(maxLength + 1, newContent.length(), "the message is not 1001 characters");

        // Set new content
        try {
            dn.setContent(newContent);

            fail("Should have thrown an exception!");
        } catch (Exception e) {

            Class<?> expected = ContentTooLongException.class;
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null.");

        }
    }

    /**
     * Try to set the content to some weird content: one period ".".
     *
     * @utp.description Tests whether setting the contents of a {@code Deathnote} to a string containing a single period
     * succeeds.
     * @utp.test_items {@code Deathnote}
     * @utp.input_specs -
     * @utp.output_specs Whether setting the contents of a {@code Deathnote} to {@code "."} succeeds.
     * @utp.env_needs -
     */
    @Test
    public void editContentOddContentTest1() {
        // Set new content
        String newContent = ".";
        try {
            dn.setContent(newContent);
        } catch (Exception e) {
            fail("An exception was thrown!");
        }

        // Check if the content has been set correctly
        assertEquals(newContent, dn.getContent(), "The content is not the same!");
    }

    /**
     * Try to set the content to some weird content: empty string "".
     *
     * @utp.description Tests whether setting the contents of a {@code Deathnote} to the empty string succeeds.
     * @utp.test_items {@code Deathnote}
     * @utp.input_specs -
     * @utp.output_specs Whether setting the contents of a {@code Deathnote} to {@code ""} succeeds.
     * @utp.env_needs -
     */
    @Test
    public void editContentOddContentTest2() {
        // Set some other content
        String newContent = "New content";
        try {
            dn.setContent(newContent);
        } catch (Exception e) {
            fail("An exception was thrown!");
        }

        // Check if the content has been set correctly
        assertEquals(newContent, dn.getContent(), "The content is not the same!");

        // Set empty string as content
        newContent = "";
        try {
            dn.setContent(newContent);
        } catch (Exception e) {
            fail("An exception was thrown!");
        }

        // Check if the content has been set correctly
        assertEquals(newContent, dn.getContent(), "The content should have been empty!");
    }
}

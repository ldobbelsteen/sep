package org.lukos.model.winhandler;

import org.junit.jupiter.api.Test;
import org.lukos.model.rolesystem.Group;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@code WinJester}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 07-03-2022
 */
public class WinJesterTest extends WinHandlerTest {

    @Override
    protected WinHandler createNewInstance() {
        return new WinJester();
    }

    @Override
    protected WinHandler createNewInstance(WinHandler next) {
        return new WinJester(next);
    }

    /**
     * Tests the constructor without parameters.
     *
     * @utp.description Tests whether the constructor behaves as intended.
     */
    @Test
    public void testEmptyConstructor() {
        assertNotNull(instance.getGroup(), "Group should not be null.");
        assertEquals(Group.JESTER, instance.getGroup(), "Group should be jester.");
        assertNull(instance.getNext(), "Next should be null.");
    }

    /**
     * Tests the constructor with parameter.
     *
     * @utp.description Tests whether the constructor behaves as intended.
     */
    @Test
    public void testConstructor() {
        WinHandler instance = new WinJester(this.instance);
        assertNotNull(instance.getGroup(), "Group should not be null.");
        assertEquals(Group.JESTER, instance.getGroup(), "Group should be jester.");
        assertNotNull(instance.getNext(), "Next should not be null.");
        assertEquals(this.instance, instance.getNext(), "Instance should have this.instance as next.");
    }

    /** @utp.description Tests whether the {@code checkWin()} function behaves as intended. */
    @Test
    public void checkWinTest() {
        try {
            assertNull((new WinJester()).checkWin(new ArrayList<>()));
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether the constructor with 1 parameter behaves as intended.
     */
    @Override @Test
    public void testConstructorSingleParameter() {
        super.testConstructorSingleParameter();
    }

    /**
     * @utp.description Tests whether the constructor with multiple parameters behaves as intended.
     */
    @Override @Test
    public void testConstructorMultiParameter() {
        super.testConstructorMultiParameter();
    }

    /**
     * @utp.description Tests whether the constructor with 1 parameter behaves as intended.
     */
    @Override @Test
    public void testInstanceConstructorNoParameter() {
        super.testInstanceConstructorNoParameter();
    }

    /**
     * @utp.description Tests whether the constructor with multiple parameters behaves as intended.
     */
    @Override @Test
    public void testInstanceConstructorSingleParameter() {
        super.testInstanceConstructorSingleParameter();
    }

    /**
     * @utp.description Tests whether the {@code setNext()} method behaves as intended.
     */
    @Override @Test
    public void testSetNext() {
        super.testSetNext();
    }

    /**
     * @utp.description Tests whether the {@code setNext()} method throws an error when someone tries to cut off the
     * handler chain.
     */
    @Override @Test
    public void testSetNextException() {
        super.testSetNextException();
    }

    /**
     * @utp.description Tests whether the function {@code checkWin()} behaves as intended.
     */
    @Override @Test
    public void testCheckWin() {
        super.testCheckWin();
    }

    /** @utp.description Tests whether the function {@code listGroups()} returns the right groups. */
    @Override @Test
    public void listGroupsTest() {
        super.listGroupsTest();
    }
}

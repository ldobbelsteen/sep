package org.lukos.controller.response;

import org.junit.jupiter.api.Test;
import org.lukos.model.rolesystem.Group;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link GroupResponse}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 20-04-2022
 */
public class GroupResponseTest {

    /** @utp.description Tests whether the constructor initializes the variables correctly. */
    @Test
    public void constructorTest() {
        GroupResponse response = new GroupResponse(new ArrayList<>());
        assertEquals(new ArrayList<>(), response.getPlayersGroups(), "PlayerGroup should be empty");
        assertNull(response.getMessage(), "Message should be null");
    }

    /** @utp.description Tests whether the {@code getPlayersGroups()} returns the right value. */
    @Test
    public void getPlayersGroupsTest() {
        GroupResponse response = new GroupResponse(new ArrayList<>());
        assertEquals(new ArrayList<>(), response.getPlayersGroups(), "Should be an empty list.");

        List<GroupEntry> groupEntries = new ArrayList<>();
        groupEntries.add(new GroupEntry(0, Group.TOWNSPEOPLE));
        groupEntries.add(new GroupEntry(66, null));
        response = new GroupResponse(groupEntries);

        List<GroupEntry> playerGroups = response.getPlayersGroups();
        assertEquals(2, playerGroups.size(), "Should be the same size.");
        GroupEntry groupEntry1 = playerGroups.get(0);
        assertEquals(0, groupEntry1.id(), "ID should be 0.");
        assertEquals(Group.TOWNSPEOPLE, groupEntry1.group(), "ID should be Townspeople.");
        GroupEntry groupEntry2 = playerGroups.get(1);
        assertEquals(66, groupEntry2.id(), "ID should be 66.");
        assertNull(groupEntry2.group(), "ID should be null.");
    }
}

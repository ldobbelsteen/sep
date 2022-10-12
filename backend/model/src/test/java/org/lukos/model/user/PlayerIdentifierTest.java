package org.lukos.model.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link PlayerIdentifier}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 20-04-2022
 */
public class PlayerIdentifierTest {

    /**
     * @utp.description Tests whether the {@code equals()} function behaves as intended.
     */
    @Test
    public void equalsTest() {
        PlayerIdentifier id1 = new PlayerIdentifier(1, 1);
        PlayerIdentifier id2 = new PlayerIdentifier(2, 1);
        PlayerIdentifier id3 = new PlayerIdentifier(1, 2);
        PlayerIdentifier id4 = new PlayerIdentifier(2, 2);
        PlayerIdentifier copyID1 = new PlayerIdentifier(1, 1);

        assertTrue(id1.equals(copyID1), "Should be the same as copy.");
        assertFalse(id1.equals(id2), "ID1 and ID2 should differ.");
        assertFalse(id1.equals(id3), "ID1 and ID3 should differ.");
        assertFalse(id1.equals(id4), "ID1 and ID4 should differ.");
        assertFalse(id1.equals(new IssuerSub("issuer", "sub")), "ID1 should be different from an IssuerSub.");
    }
}

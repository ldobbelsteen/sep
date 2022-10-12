package org.lukos.model.actionsystem;

import org.lukos.model.user.PlayerIdentifier;

/**
 * Record for all information that the player can give regarding the action
 *
 * @param playerIdentifier  Unique identifier of the player performing the action
 * @param data              Encapsulated data relevant for the action
 * @author Rick van der Heijden (1461923)
 * @since 11-03-2022
 */
public record PreActionDT(PlayerIdentifier playerIdentifier, ActionEnc data) {

    @Override
    public boolean equals(Object o) {
        if (o instanceof PreActionDT otherObject) {
            return  this.playerIdentifier.equals(otherObject.playerIdentifier) &&
                    this.data.equals(otherObject.data);
        }
        return false;
    }
}

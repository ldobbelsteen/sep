package org.lukos.model.rolesystem.roles.doubleroles;

import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.config.CharacterConfig;
import org.lukos.model.config.GroupConfig;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.instances.IInstance;
import org.lukos.model.rolesystem.Action;
import org.lukos.model.rolesystem.DoubleRole;
import org.lukos.model.rolesystem.RoleActionInformation;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.util.List;

/**
 * Double role: Lover
 * <p>
 * The Lovers want to win together. If they are in the same goal group, they win when all players outside that goal
 * group have been eliminated. If the Lovers are in different goal groups, the Lovers win if they eliminate all other
 * players. Lovers can never vote for each other during the lynch. Furthermore, if one Lover dies, the other(s) will
 * follow suit and die shortly after.
 *
 * @author Lucas Gether-Rønning
 * @since 27-02-2022
 */
public class Lover extends DoubleRole {
    //variable keeping track of which player(s) is the lover of the player with this role
    private final List<Player> lover;

    /**
     * Constructs a {@code Lover}
     *
     * @param lover player(s) that the holder of this role is in a couple with
     */
    public Lover(List<Player> lover) {
        super(CharacterConfig.LOVER.getCharacter(), GroupConfig.LOVER.getGroup());
        this.lover = lover;
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        throw new UnsupportedOperationException("The Lover cannot initialize its actions yet!");
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        throw new UnsupportedOperationException("The Lover cannot perform actions yet!");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
            throws SQLException, GameException, ReflectiveOperationException {
        throw new UnsupportedOperationException("The Lover does not have action information yet!");
    }

    @Override
    public List<Action> getActions() {
        throw new UnsupportedOperationException("The Lover does not have actions yet!");
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
        throw new UnsupportedOperationException("The Lover cannot replenish its actions yet!");
    }
}

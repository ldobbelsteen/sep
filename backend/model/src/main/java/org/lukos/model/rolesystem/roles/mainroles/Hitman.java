package org.lukos.model.rolesystem.roles.mainroles;

import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.config.CharacterConfig;
import org.lukos.model.config.GroupConfig;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.instances.IInstance;
import org.lukos.model.rolesystem.Action;
import org.lukos.model.rolesystem.MainRole;
import org.lukos.model.rolesystem.RoleActionInformation;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.util.List;

/**
 * Role: Hitman
 * <p>
 * The Hitman's goal is to let a specific person be lynched. If the Hitman succeeds in making this player die by means
 * of a lynch, the Hitman wins immediately. If the chosen player is killed in any other way, the Hitman will get the
 * double role of Jester.
 *
 * @author Lucas Gether-Rønning
 * @author Martijn van Andel (1251104)
 * @since 26-02-22
 */
public class Hitman extends MainRole {
    private final Player hit; // the target the hitman wants to kill

    /**
     * Constructs a {@code Hitman}
     *
     * @param hit target of the hitman
     */
    public Hitman(Player hit) {
        super(CharacterConfig.HITMAN.getCharacter(), GroupConfig.HITMAN.getGroup());
        this.hit = hit;
    }

    /**
     * This constructor is only called during role assignment. After which, a new Hitman is added to the player, which
     * does have a {@code hit} assigned.
     */
    public Hitman() {
        this(null);
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        throw new UnsupportedOperationException("The Hitman cannot initialize its actions yet!");
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        throw new UnsupportedOperationException("The Hitman cannot perform actions yet!");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
            throws SQLException, GameException, ReflectiveOperationException {
        throw new UnsupportedOperationException("The Hitman does not have action information yet!");
    }

    @Override
    public List<Action> getActions() {
        throw new UnsupportedOperationException("The Hitman does not have actions yet!");
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
        throw new UnsupportedOperationException("The Hitman cannot replenish its actions yet!");
    }
}

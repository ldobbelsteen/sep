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

import java.sql.SQLException;
import java.util.List;

/**
 * Role: Werefolf-framer
 * <p>
 * Part of the werewolves goal group. Once per evening, the Werewolf Framer can decide to frame another player. If a
 * player dies between the moment at which they were framed and the start of the next evening, the obituary will show
 * that the player was a werewolf, instead of their real role.
 *
 * @author Lucas Gether-Rønning
 * @since 21-02-2022
 */
public class WerewolfFramer extends MainRole {
    private boolean framed; //variable keeping track of if the WWframer has used their ability

    /**
     * Constructs a {@code WerewolfFramer}
     *
     * @param framed if the framer has used their ability
     */
    public WerewolfFramer(boolean framed) {
        super(CharacterConfig.WEREWOLF_FRAMER.getCharacter(), GroupConfig.WEREWOLF_FRAMER.getGroup());
        this.framed = framed;
    }

    /** Default constructor. */
    public WerewolfFramer() {
        // TODO: Read from config file
        this(false);
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        throw new UnsupportedOperationException("The Werewolf Framer cannot initialize its actions yet!");
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        throw new UnsupportedOperationException("The Werewolf Framer cannot perform actions yet!");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
            throws SQLException, GameException, ReflectiveOperationException {
        throw new UnsupportedOperationException("The Werewolf Framer does not have action information yet!");
    }

    @Override
    public List<Action> getActions() {
        throw new UnsupportedOperationException("The Werewolf Framer does not have actions yet!");
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
        throw new UnsupportedOperationException("The Werewolf Framer cannot replenish its actions yet!");
    }
}

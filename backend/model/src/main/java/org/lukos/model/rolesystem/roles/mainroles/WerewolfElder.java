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
 * Role: Werefolf-elder
 * <p>
 * Part of the werewolves goal group. Once per game, the Werewolf Elder can bite another player. This player will be
 * turned into a Werewolf Cub. This Werewolf Cub will grow into a Werewolf after one in-game day.
 *
 * @author Lucas Gether-Rønning
 * @since 21-02-2022
 */
// TODO: Fix naming of variables
public class WerewolfElder extends MainRole {
    private boolean bitten; //variable keeping track of if the WWelder has used their ability

    /**
     * Constructs a {@code WerewolfElder}
     *
     * @param bitten if a werewolf elder has used their ability
     */
    public WerewolfElder(boolean bitten) {
        super(CharacterConfig.WEREWOLF_ELDER.getCharacter(), GroupConfig.WEREWOLF_ELDER.getGroup());
        this.bitten = bitten;
    }

    /** Default constructor. */
    public WerewolfElder() {
        // TODO: Read from config file
        this(false);
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        throw new UnsupportedOperationException("The Werewolf Elder cannot initialize its actions yet!");
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        throw new UnsupportedOperationException("The Werewolf Elder cannot perform actions yet!");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
            throws SQLException, GameException, ReflectiveOperationException {
        throw new UnsupportedOperationException("The Werewolf Elder does not have action information yet!");
    }

    @Override
    public List<Action> getActions() {
        throw new UnsupportedOperationException("The Werewolf Elder does not have actions yet!");
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
        throw new UnsupportedOperationException("The Werewolf Elder cannot replenish its actions yet!");
    }
}

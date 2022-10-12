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
 * Role: Werewolf-cleaner
 * <p>
 * Part of the werewolves goal group and has two abilities which can be used separately, namely cleaning the death note
 * of a player and a clean kill (killing everyone present at a location and wiping the killed players' death note)
 *
 * @author Lucas Gether-Rønning
 * @since 21-02-2022
 */
// TODO: Fix naming of variables
public class WerewolfCleaner extends MainRole {
    private boolean cleaned; //variable keeping track of if the WWcleaner has used their cleaning-ability
    private boolean eaten; //variable keeping track of if the WWcleaner has used their eating-ability

    /**
     * Constructs a {@code WerewolfCleaner}
     *
     * @param cleaned if the cleaning ability is used
     * @param eaten   if the eating ability is used
     */
    public WerewolfCleaner(boolean cleaned, boolean eaten) {
        super(CharacterConfig.WEREWOLF_CLEANER.getCharacter(), GroupConfig.WEREWOLF_CLEANER.getGroup());
        this.cleaned = cleaned;
        this.eaten = eaten;
    }

    /** Default constructor */
    public WerewolfCleaner() {
        // TODO: Read from config file
        this(false, false);
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        throw new UnsupportedOperationException("The Werewolf Cleaner cannot initialize its actions yet!");
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        throw new UnsupportedOperationException("The Werewolf Cleaner cannot perform actions yet!");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
            throws SQLException, GameException, ReflectiveOperationException {
        throw new UnsupportedOperationException("The Werewolf Cleaner does not have action information yet!");
    }

    @Override
    public List<Action> getActions() {
        throw new UnsupportedOperationException("The Werewolf Cleaner does not have actions yet!");
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
        throw new UnsupportedOperationException("The Werewolf Cleaner cannot replenish its actions yet!");
    }
}

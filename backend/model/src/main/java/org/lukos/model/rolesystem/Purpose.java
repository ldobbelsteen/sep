package org.lukos.model.rolesystem;

import lombok.Getter;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.instances.IInstance;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.SQLException;
import java.util.List;

/**
 * Represents a purpose
 * <p>
 * Every job and role has purpose, essentially the abilities and descripton of these roles/jobs
 *
 * @author Lucas Gether-Rønning
 * @since 21-02-2022
 */
@Getter
public abstract class Purpose {
    private final CharacterType character; //character associated with the purpose

    /**
     * Constructs a {@code Purpose}.
     *
     * @param character The character type of role/job
     */
    public Purpose(CharacterType character) {
        this.character = character;
    }

    /**
     * Method to initialize the character's actions.
     *
     * @param player The player for whom the actions are initialized.
     * @throws GameException when a game-logic operation fails
     * @throws SQLException  when a database operation fails
     */
    public abstract void initializeActions(PlayerIdentifier player) throws SQLException, GameException;

    /**
     * Method to perform an action
     * <p>
     * A player with a certain role/job performs an action associated with this role/job
     *
     * @param data   The data associated with the action to be performed, that can be given by a player
     * @param action the {@code Action} that needs to be performed
     * @throws GameException                when a game-logic operation fails
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public abstract void performAction(PreActionDT data, Action action)
            throws GameException, SQLException, ReflectiveOperationException;

    /**
     * Returns a {@code List} of information about the {@code Action}s the purpose can take.
     *
     * @param instance         the {@code Instance} for which the information is retrieved
     * @param playerIdentifier the ID of the {@code Player} who requests the information
     * @return a {@code List} of information about the {@code Action}s the purpose can take
     * @throws GameException                when a game-logic operation fails
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public abstract List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
            throws SQLException, GameException, ReflectiveOperationException;

    /**
     * Returns a {@code List} of {@code Action}s.
     *
     * @return a {@code List} of {@code Action}s.
     */
    public abstract List<Action> getActions();

    /**
     * Method to reset the action parameters of a purpose.
     *
     * @param gameSpeed        The gameSpeed, which could affect the amounts for parameters.
     * @param playerIdentifier the {@code PlayerIdentifier} of the {@code Player} of which the actions are replenished
     * @throws GameException when a game-logic operation fails
     * @throws SQLException  when a database operation fails
     */
    public abstract void replenishAction(int gameSpeed, PlayerIdentifier playerIdentifier)
            throws GameException, SQLException;
}

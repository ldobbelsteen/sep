package org.lukos.model.user.player;

import org.lukos.model.actionsystem.ActionEnc;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.notes.Deathnote;
import org.lukos.model.rolesystem.*;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.lukos.model.user.player.PlayerDBHelper.*;

/**
 * The player class contains information for one player for one game instance
 *
 * @author Valentijn van den Berg (1457446)
 * @since 21-02-2022
 */
public class Player {

    /** Unique player id */
    private final PlayerIdentifier playerIdentifier;

    public Player(PlayerIdentifier playerIdentifier) {
        this.playerIdentifier = playerIdentifier;
    }

    /**
     * Constructor
     */
    public Player(int gameId, int userId) throws SQLException, GameException {
        this(new PlayerIdentifier(gameId, userId));
    }

    public MainRole getMainRole() throws SQLException, ReflectiveOperationException, GameException {
        return getMainRolePlayerByID(this.playerIdentifier);
    }

    public void setMainRole(MainRole mainRole) throws SQLException, ReflectiveOperationException {
        setMainRolePlayerByID(this.playerIdentifier, mainRole);
    }

    public ArrayList<DoubleRole> getDoubleRoles() throws SQLException, ReflectiveOperationException {
        return getDoubleRolesPlayerByID(this.playerIdentifier);
    }

    public void addDoubleRole(DoubleRole doubleRole) throws SQLException {
        addDoubleRolePlayerByID(this.playerIdentifier, doubleRole);
    }

    public void addJob(Job job) throws SQLException {
        addJobPlayerByID(this.playerIdentifier, job);
    }

    public boolean removeJob(Job job) throws SQLException {
        return removeJobPlayerByID(this.playerIdentifier, job);
    }

    public ArrayList<Job> getJobs() throws SQLException, ReflectiveOperationException {
        return getJobsPlayerByID(this.getPlayerIdentifier());
    }

    public ArrayList<Purpose> getPurposes() throws ReflectiveOperationException, SQLException, GameException {
        ArrayList<Purpose> purposes = new ArrayList<>();
        purposes.add(getMainRole());
        purposes.addAll(getDoubleRoles());
        purposes.addAll(getJobs());
        return purposes;
    }

    public int getHouse() throws SQLException, GameException {
        return getHousePlayerByID(this.playerIdentifier);
    }

    public void setHouse(int houseID) throws SQLException {
        setHousePlayerByID(this.playerIdentifier, houseID);
    }

    public Deathnote getDeathnote() throws SQLException, GameException {
        return getDeathNotePlayerByID(this.playerIdentifier);
    }

    public void setDeathNote(Deathnote deathnote) throws SQLException {
        setDeathNotePlayerByID(this.playerIdentifier, deathnote);
    }

    /**
     * Returns whether this player is alive or not.
     *
     * @return Whether this player is alive or not
     */
    public boolean alive() throws SQLException, GameException {
        return isAlivePlayer(getPlayerIdentifier());
    }

    /**
     * Update the content of the deathnote.
     *
     * @param newContent String containing the new content of the note.
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public void updateNote(String newContent) throws GameException, SQLException {
        Deathnote note = getDeathnote();
        note.setContent(newContent);
        setDeathNote(note);
    }

    /**
     * Use this function to make {@code this.player} vote on {@code target}.
     *
     * @param vid    the id of the vote.
     * @param target the player {@code this.player} votes on.
     */
    public void vote(int vid, Player target) throws GameException, SQLException, ReflectiveOperationException {
        PlayerVoteHelper.vote(getPlayerIdentifier(), vid, target.getPlayerIdentifier());
    }

    public PlayerIdentifier getPlayerIdentifier() {
        return this.playerIdentifier;
    }

    public void performAction(ActionEnc data, Action action)
            throws ReflectiveOperationException, SQLException, GameException {
        PlayerActionHelper.performAction(getPlayerIdentifier(), data, action, getPurposes());
    }

    public void setProtected(boolean newValue) throws SQLException {
        updateProtected(this.playerIdentifier, newValue);
    }
}

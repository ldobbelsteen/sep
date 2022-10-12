package org.lukos.model.instances;

import org.lukos.database.InstanceDB;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.instances.NoSuchInstanceException;
import org.lukos.model.exceptions.user.NoSuchPlayerException;
import org.lukos.model.user.player.Player;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.lukos.model.instances.util.GeneralInstanceHelper.*;

/**
 * Holds publicly available information about the current state of the game, namely the day, phase, and the players that
 * are still alive.
 *
 * @author Martijn van Andel (1251104)
 * @since 21-02-2022
 */
public class InstanceState {

    private static final int GAME_SPEED_INTERVAL = 4;

    private final int iid;

    public InstanceState(int iid) {
        this.iid = iid;
    }

    public InstanceState(int iid, List<PlayerIdentifier> players) throws SQLException, GameException {
        this.iid = iid;
        setAliveIdentifiers(new ArrayList<>(players));
        setPhase(getPhaseFromTime());
        setDay(getPhase() == DayPhase.DAY ? 1 : 0);
    }

    public int getDay() throws SQLException {
        ResultSet result = InstanceDB.findInstanceByID(this.iid);
        if (!result.next()) {
            throw new SQLException("That ID is invalid!");
        }
        return result.getInt("day");
    }

    public void setDay(int day) throws SQLException {
        InstanceDB.modifyDay(this.iid, day);
    }

    public DayPhase getPhase() throws SQLException, NoSuchInstanceException {
        ResultSet result = InstanceDB.findInstanceByID(this.iid);

        // retrieve the first (and only) entry
        if (result.next()) {
            String phase = result.getString("dayPhase");
            return DayPhase.valueOf(phase);
        } else {
            throw new NoSuchInstanceException("Instance does not exist.");
        }
    }

    // TODO: determine whether to get the day phase from time, or to use db for it
    public void setPhase(DayPhase phase) throws SQLException {
        InstanceDB.modifyPhase(this.iid, phase);
    }

    public int getGameSpeed() throws SQLException {
        return getDay() == 0 ? 0 : ((getDay() - 1) / GAME_SPEED_INTERVAL);
    }

    public void setAliveIdentifiers(List<PlayerIdentifier> players) throws SQLException, NoSuchPlayerException {
        InstanceDB.setAlivePlayer(this.iid, players);
    }

    /**
     * Returns a list with all players that are alive.
     *
     * @return a list with all players that are alive
     */
    public List<PlayerIdentifier> getAlive() throws SQLException {
        return InstanceDB.getAlivePlayers(this.iid);
    }

    public void setAlive(List<Player> players) throws SQLException, NoSuchPlayerException {
        setAliveIdentifiers(players.stream().map(Player::getPlayerIdentifier).toList());
    }

    /**
     * Kills a player.
     *
     * @param player The player to be revived
     * @throws NoSuchPlayerException if the player is not alive, or does not exist
     */
    public void killPlayer(Player player) throws GameException, SQLException {
        if (!InstanceDB.isAlivePlayer(player.getPlayerIdentifier())) {
            throw new NoSuchPlayerException("The player trying to be killed does not exist or is already dead.");
        }
        PlayerIdentifier playerID = player.getPlayerIdentifier();
        InstanceDB.killPlayer(playerID);
        int userID = player.getPlayerIdentifier().userID();

        toggleWriteAccessAliveChats(userID, false);
        addPlayerToDeceasedChat(playerID);
    }

    /**
     * Revives a player. Prerequisite: {@code player} is part of the instance.
     *
     * @param player The player to be revived
     */
    void revivePlayer(Player player) throws SQLException, GameException, ReflectiveOperationException {
        List<PlayerIdentifier> alive = this.getAlive();
        PlayerIdentifier playerID = player.getPlayerIdentifier();
        alive.add(playerID);
        setAliveIdentifiers(alive);
        int userID = player.getPlayerIdentifier().userID();

        toggleWriteAccessAliveChats(userID, true);

        removePlayerFromDeceasedChat(player);
    }

    /**
     * Returns the day phase based on the current time. If a timeOverride >= 0 is used, that time is used instead
     *
     * @param timeOverride the time to test. If negative, ignore
     * @return DayPhase  corresponding to real time.
     */
    public static DayPhase getPhaseFromTime(int timeOverride) {
        int currentMinute;

        if (timeOverride >= 0) {
            // This is a test. Ignore real time.
            currentMinute = timeOverride;
        } else {
            // This is not a test. Use real time.
            LocalDateTime now = LocalDateTime.now();
            currentMinute = now.getHour() * 60 + now.getMinute();
        }

        // Representation of end of day phases, converted to minutes
        int END_OF_NIGHT = 8 * 60 + 25;
        int END_OF_MORNING = 8 * 60 + 30;
        int END_OF_DAY = 18 * 60;
        int END_OF_VOTE = 20 * 60;
        int END_OF_EXECUTION = 20 * 60 + 5;

        // Determine day phase
        if (currentMinute < END_OF_NIGHT) {
            return DayPhase.NIGHT;
        }
        if (currentMinute < END_OF_MORNING) {
            return DayPhase.MORNING;
        }
        if (currentMinute < END_OF_DAY) {
            return DayPhase.DAY;
        }
        if (currentMinute < END_OF_VOTE) {
            return DayPhase.VOTE;
        }
        if (currentMinute < END_OF_EXECUTION) {
            return DayPhase.EXECUTION;
        }

        return DayPhase.EVENING;
    }

    /**
     * Returns the day phase based on the current time.
     *
     * @return DayPhase  according to real time
     */
    public static DayPhase getPhaseFromTime() {
        return getPhaseFromTime(-1);
    }

    /**
     * Moves state to next phase. Updates day if necessary.
     */
    void nextPhase() throws SQLException, NoSuchInstanceException {
        setPhase(getPhase().next());
        if (getPhase() == DayPhase.MORNING) {
            // TODO: make increment function??
            setDay(getDay() + 1);
        }
    }
}

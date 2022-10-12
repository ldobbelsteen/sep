package org.lukos.model.instances;

import lombok.Getter;
import org.lukos.database.*;
import org.lukos.model.actionsystem.ActionDT;
import org.lukos.model.actionsystem.ActionDTComparator;
import org.lukos.model.actionsystem.ActionManager;
import org.lukos.model.actionsystem.SuccessorType;
import org.lukos.model.actionsystem.actions.KillPlayers;
import org.lukos.model.chatsystem.Chat;
import org.lukos.model.chatsystem.ChatIdentifier;
import org.lukos.model.chatsystem.ChatManager;
import org.lukos.model.chatsystem.ChatType;
import org.lukos.model.events.NextPhaseEvent;
import org.lukos.model.events.WinEvent;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.NoPermissionException;
import org.lukos.model.exceptions.instances.GameAlreadyStartedException;
import org.lukos.model.exceptions.instances.NotEnoughPlayersException;
import org.lukos.model.exceptions.instances.TooManyPlayersException;
import org.lukos.model.exceptions.location.BridgeDoesNotExistException;
import org.lukos.model.exceptions.location.NoSuchLocationException;
import org.lukos.model.exceptions.user.NoSuchPlayerException;
import org.lukos.model.exceptions.user.NoSuchRoleException;
import org.lukos.model.exceptions.voting.NoSuchVoteException;
import org.lukos.model.exceptions.voting.VotingException;
import org.lukos.model.location.Bridge;
import org.lukos.model.location.House;
import org.lukos.model.location.Location;
import org.lukos.model.location.states.Repaired;
import org.lukos.model.notes.Deathnote;
import org.lukos.model.rolesystem.Group;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.player.Player;
import org.lukos.model.voting.*;
import org.lukos.model.winhandler.WinHandler;
import org.lukos.model.winhandler.WinTownspeople;
import org.lukos.model.winhandler.WinWolves;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.lukos.model.instances.util.GeneralInstanceHelper.*;

/**
 * Maintains the state of a single game instance.
 *
 * @author Martijn van Andel (1251104)
 * @author Rick van der Heijden (1461923)
 * @since 21-02-2022
 */
class Instance implements IInstance {
    /**
     * Static parameters for this instance
     */
    // TODO: Configure
    private static final int MIN_PLAYERS = 2; // Default: 12
    private static final int MAX_PLAYERS = 50; // Default: 50
    private static int SEED;
    /**
     * Unique identifier of this instance
     */
    @Getter
    private final int iid;

    Instance(int iid, int SEED) {
        this.iid = iid;
        this.SEED = SEED;
    }

    /**
     * Creates an instance with {@code caller} as gamemaster.
     *
     * @param caller the user creating a new instance.
     */
    @Deprecated
    Instance(int iid, int caller, int SEED) throws SQLException {
        this.iid = iid;
        this.SEED = SEED;

        // creator of the instance, becomes game master
//        uid = caller;
//
//        players = new ArrayList<>();
//        ongoingVotes = new ArrayList<>();
//        toExecute = new ArrayList<>();

//        // TODO: Initialize bridges with configurable names
//        bridges = new ArrayList<>();
        InstanceDB.setGameMasterByInstance(this.iid, caller);

        // create Bridges
        // this already stores them in the database, so we do not need to add them to the DB manually.
        new Bridge(this.iid, "Sint-vlaflip oversteekbrug voor wandelende teddyberen");
        new Bridge(this.iid, "De heen en weerwolfbrug");
    }

    /**
     * Returns the UID of the game master.
     *
     * @return UUID uid of the game master
     * @throws SQLException if something went wrong in the database.
     */
    public int getGameMaster() throws SQLException {
        return InstanceDB.getGameMaster(this.iid);
    }

    /**
     * Removes a vote from the ongoingVotes list
     *
     * @param vote the vote to be removed
     */
    public void removeOngoingVote(Vote vote) throws SQLException {
        removeOngoingVote(vote.getVid());
    }

    /**
     * Removes a vote from the ongoingVotes list
     *
     * @param voteID the ID of the vote to be removed
     */
    public void removeOngoingVote(int voteID) throws SQLException {
        VoteDB.deleteVoteByID(voteID);
    }

    /**
     * Sets the default parameters for InstanceState, including setting all players to be alive.
     */
    public void initializeInstanceState(InstanceState instanceState) throws SQLException {
        // set all players to be alive
        // UPDATE playerState=alive WHERE instanceId= this.iid

        // set default values for day, dayphase

        InstanceDB.initializeInstanceState(this.iid);
    }

    public String getGameName() throws SQLException {
        return InstanceDB.getGameNameByInstanceID(this.iid);
    }

    public List<Integer> getBridges() throws SQLException, BridgeDoesNotExistException {
        return LocationDB.getBridgesByInstance(this.iid);
    }

    public boolean removeBridge(Bridge bridge) throws SQLException {
        return LocationDB.removeBridgeByInstance(this.iid, bridge.getId());
    }

    public List<PlayerIdentifier> getToBeExecuted() throws SQLException {
        return InstanceDB.getToBeExecuted(this.iid);
    }

    /**
     * Adds the {@code player} to the to-be-executed list.
     *
     * @param playerID the list of IDs of players to be executed.
     * @throws SQLException if something database related went wrong.
     */
    public void addToBeExecuted(PlayerIdentifier playerID) throws SQLException {
        InstanceDB.modifyExecuted(this.iid, playerID, true);
    }

    /**
     * Adds every player in {@code players} to the to-be-executed list.
     *
     * @param players the list of players to be executed.
     * @throws SQLException if something database related went wrong.
     */
    public void addToBeExecuted(List<PlayerIdentifier> players) throws SQLException {
        List<Player> playerList = playerIdentifierListToPlayerList(players);
        for (Player player : playerList) {
            addToBeExecuted(player.getPlayerIdentifier());
        }
    }

    public void removeToBeExecuted(Player player) throws SQLException {
        InstanceDB.modifyExecuted(this.iid, player.getPlayerIdentifier(), false);
    }

    /**
     * Returns the general winhandler that can be used by instances.
     *
     * @return the general winhandler
     */
    public static WinHandler getWinHandler() {
        return new WinWolves(new WinTownspeople());
    }

    //////////   Player list related   //////////

    /**
     * Converts a list of {@code Player} to a list of {@code PlayerIdentifier}.
     *
     * @param playerList {@code List<Player>}
     * @return playerList converted to a list of {@code PlayerIdentifier}.
     */
    public List<PlayerIdentifier> playerListToPlayerIdentifierList(List<Player> playerList) {
        List<PlayerIdentifier> playerIdentifierList = new ArrayList<>();
        playerList.forEach(player -> playerIdentifierList.add(player.getPlayerIdentifier()));
        return playerIdentifierList;
    }

    /**
     * Converts a list of {@code PlayerIdentifier} to a list of {@code Player}.
     *
     * @param playerIdentifierList {@code List<PlayerIdentifier>}
     * @return playerList converted to a list of {@code Player}.
     */
    public List<Player> playerIdentifierListToPlayerList(List<PlayerIdentifier> playerIdentifierList) {
        List<Player> playerList = new ArrayList<>();
        for (PlayerIdentifier p : playerIdentifierList) {
            playerList.add(new Player(p));
        }
        return playerList;
    }

    /**
     * Returns a list of alive players.
     *
     * @return List of players who are alive
     */
    public List<Player> alivePlayers() throws SQLException {
        List<PlayerIdentifier> playerIdentifiers = this.getInstanceState().getAlive();

        return playerIdentifierListToPlayerList(playerIdentifiers);
    }

    /**
     * Returns a list of dead players
     *
     * @return List of players who are dead
     */
    public List<Player> deadPlayers() throws SQLException {
        return playerIdentifierListToPlayerList(InstanceDB.getDeadPlayers(this.iid));
    }

    /**
     * Returns a list of homeless players
     *
     * @return List of players with a burned house
     */
    public List<Player> homelessPlayers() throws SQLException {
        return playerIdentifierListToPlayerList(HouseDB.getHomelessPlayers(this.iid));
    }

    /**
     * Returns a list of players with an intact house
     *
     * @return List of players with an intact house
     */
    public List<Player> homeOwners() throws SQLException {
        return playerIdentifierListToPlayerList(HouseDB.getHomeOwners(this.iid));
    }

    /**
     * Kills a player
     *
     * @param player the player that will be killed
     * @throws NoSuchPlayerException if the targeted player does not exist, or is already dead
     */
    public void killPlayer(Player player) throws GameException, SQLException, ReflectiveOperationException {
        this.getInstanceState().killPlayer(player);

        Deathnote note = player.getDeathnote();
        note.setChangeable(false);
        player.setDeathNote(note);

        // If a successor dies we remove the successor from the database, as they cannot be a successor anymore
        PlayerIdentifier playerID = player.getPlayerIdentifier();
        Map<SuccessorType, PlayerIdentifier> successors = SuccessorDB.getSuccessors(this.iid);
        if (successors.containsValue(playerID)) {
            for (SuccessorType type : successors.keySet()) {
                if (successors.get(type).equals(playerID)) {
                    SuccessorDB.removeSuccessor(this.iid, type);
                }
            }
        }

        // Give successor the job in case of death of the current holder of the job
        assignSuccessor(player, this.iid, getGameMaster());
    }

    /**
     * Revives a player
     *
     * @param player the player that will be revived
     * @throws NoSuchPlayerException if the targeted player does not exist, or is already alive
     */
    public void revivePlayer(Player player) throws GameException, SQLException, ReflectiveOperationException {
        if (getPlayerList().stream().noneMatch(p -> p.getPlayerIdentifier().equals(player.getPlayerIdentifier()))) {
            throw new NoSuchPlayerException("Player does not exist");
        } else if (alivePlayers().stream().anyMatch(p -> p.getPlayerIdentifier().equals(player.getPlayerIdentifier()))) {
            throw new NoSuchPlayerException("Player is already alive");
        }

        player.getDeathnote().setChangeable(true);

        getInstanceState().revivePlayer(player);
    }

    /**
     * Removes a player from the current game. This effectively reconfigures the user to not participate in any game.
     *
     * @return true if the player has been deleted and no exception is thrown
     * @throws SQLException Exception thrown when db operation fails
     */
    public boolean removePlayer(Player player) throws SQLException {
        PlayerIdentifier pi = player.getPlayerIdentifier();
        PlayerDB.deletePlayerByID(pi.userID(), pi.instanceID());
        return true;
    }

    /**
     * Kicks a player from the player list
     *
     * @param caller must be gamemaster
     * @param player player to be removed
     * @throws NoPermissionException if caller != uid
     * @throws NoSuchPlayerException if player \notin players
     */
    public void kickPlayer(int caller, Player player) throws GameException, SQLException {
        // Verify caller == gamemaster
        if (caller != getGameMaster()) {
            throw new NoPermissionException("Denied kick request, caller is not the gamemaster.");
        }

        // Remove player, throw exception if player doesn't exist
        if (!removePlayer(player)) { // TODO: Watch out !!
            throw new NoSuchPlayerException("Player not found in this instance.");
        }

        // TODO: Notify controllers that player has successfully been kicked from the instance

    }

    /**
     * Returns the players in the instance
     *
     * @return List of all players
     */
    public List<Player> getPlayerList() throws SQLException {
        List<PlayerIdentifier> playerIdentifiers = InstanceDB.getPlayers(this.iid);
        List<Player> players = new ArrayList<>();
        for (PlayerIdentifier playerId : playerIdentifiers) {
            players.add(new Player(playerId));
        }
        return players;
    }

    //////////   Game state related   //////////

    /**
     * Returns public info about the current game state.
     *
     * @return instanceState;
     * @throws NullPointerException if instanceState == null
     */
    public InstanceState getInstanceState() throws SQLException {
        ResultSet resultSet = InstanceDB.findInstanceByID(this.iid);

        // go to the first entry of the resultSet
        if (!resultSet.next()) {
            throw new SQLException("That ID is invalid!");
        }
        String phase = resultSet.getString("dayPhase");
        return phase == null ? null : new InstanceState(this.iid);
    }

    /**
     * Starts the game. caller must be game master and day must be 0 (lobby)
     *
     * @param caller the user issuing the game to start. Must be the gamemaster.
     * @throws NoPermissionException     if anyone but the game master calls this method.
     * @throws NotEnoughPlayersException if this method is called but there are less player than {@code MIN_PLAYERS}
     * @throws TooManyPlayersException   if this method is called but there are more players than {@code MAX_PLAYERS}
     * @throws VotingException           if something went wrong while creating votes
     */
    public void startGame(int caller) throws GameException, SQLException, ReflectiveOperationException {
        if (caller != getGameMaster()) {
            throw new NoPermissionException("startGame called by someone other than gamemaster");
        }
        if (getPlayerList().size() < MIN_PLAYERS) {
            throw new NotEnoughPlayersException("Cannot start game, not enough players.");
        }
        if (getPlayerList().size() > MAX_PLAYERS) {
            throw new TooManyPlayersException("Cannot start game, too many players.");
        }
        if (isStarted()) {
            throw new GameAlreadyStartedException("Cannot start a game that has already started.");
        }

        /* Initialize instanceState */
        // FIXME: replace use of setInstanceState
        initializeInstanceState(
                new InstanceState(this.iid, getPlayerList().stream().map(Player::getPlayerIdentifier).toList()));
        /* Assign roles */
        assignRoles(SEED, new ArrayList<>(getPlayerList()));

        /* Create chats. */
        List<ChatIdentifier> chatIdentifiers = new ArrayList<>();
        for (ChatType ct : ChatType.values()) {
            int id = ChatManager.getInstance().createChat(iid, ct);
            chatIdentifiers.add(new ChatIdentifier(id, ct));
            if (ct == ChatType.DECEASED || ct == ChatType.WOLVES) {
                Chat.openChat(id, true);
            }
        }

        /* Send every player home + add to chats */
        for (Player player : getPlayerList()) {
            // House
            House house = new House(player.getPlayerIdentifier(), Repaired.getInstance());
            house.visitPlayer(player);

            addPlayerToChats(player, chatIdentifiers);
        }

        /* Initialize bridges. */
        new Bridge(this.iid, "Sint-vlaflip oversteekbrug voor wandelende teddyberen");
        new Bridge(this.iid, "De heen en weerwolfbrug");

        /* Trigger phase updates. */
        getInstanceState().setPhase(getInstanceState().getPhase().previous());
        nextPhase();
    }

    /**
     * Ends a lobby.
     *
     * @throws SQLException                if something went wrong in the database.
     * @throws GameAlreadyStartedException if the game has already begun when this method is called.
     */
    public void stopGame() throws SQLException, GameAlreadyStartedException {
        if (isStarted()) {
            throw new GameAlreadyStartedException("Cannot stop a game that has already started.");
        }

        endGame();
    }

    /**
     * Deletes this instance from the database.
     *
     * @throws SQLException if something went wrong in the database.
     */
    public void endGame() throws SQLException {
        InstanceDB.deleteInstanceByIID(this.iid);
    }

    /**
     * Ends the game by killing all non-townspeople.
     *
     * @param caller the user issuing the game to start. Must be the gamemaster.
     * @throws NoPermissionException     if anyone but the game master calls this method.
     * @throws NotEnoughPlayersException if this method is called but there are less player than {@code MIN_PLAYERS}
     * @throws TooManyPlayersException   if this method is called but there are more players than {@code MAX_PLAYERS}
     * @throws VotingException           if something went wrong while creating votes
     */
    public void throwGame(int caller) throws GameException, SQLException, ReflectiveOperationException {
        if (caller != getGameMaster()) {
            throw new NoPermissionException("throwGame called by someone other than gamemaster");
        }

        if (!isStarted()) {
            throw new NoSuchRoleException("Cannot throw a game that has not started.");
        }

        for (Player p : alivePlayers()) {
            if (p.getMainRole().getGroup() != Group.TOWNSPEOPLE) {
                killPlayer(p);
            }
        }

        nextPhase();
    }

    /**
     * Switches to the next phase, and performs actions accordingly
     *
     * @throws VotingException if something vote-related went wrong
     */
    public void nextPhase() throws GameException, SQLException, ReflectiveOperationException {
        InstanceState instanceState = getInstanceState();

        /* Move to next phase. */
        instanceState.nextPhase();
        DayPhase phase = instanceState.getPhase();

        /* Only perform phase actions when the game has actually started (day >= 1). */
        if (instanceState.getDay() < 1) {
            return;
        }

        switch (phase) {
            case MORNING -> {
            }
            case DAY -> {
                /* Perform all actions. */
                ActionManager.performActions(this.iid);

                /* Execute players in toBeExecuted list. */
                // find the latest killPlayers action
                List<ActionDT> killActions = getKillActions();
                if (killActions.size() != 0) {
                    // Sort based on time submitted
                    killActions.sort(new ActionDTComparator());
                    PlayerIdentifier killer = killActions.get(killActions.size() - 1).preAction().playerIdentifier();

                    killMarkedPlayers(killer, new ArrayList<>(getToBeExecuted()), KillMarkedPlayers.NIGHT);
                    ActionManager.performActions(this.iid);
                }

                /* Reset protected stats. */
                for (PlayerIdentifier player : PlayerDB.getProtectedPlayers(this.iid)) {
                    PlayerDB.updateProtected(player, false);
                }

                ActionManager.performActions(this.iid);
                /* Unlock all actionMessages */
                ActionMessagesDB.unlockMessages(this.iid);

                /* Put players back at their house. */
                resetPlayerLocation();

                /* Update building progress of houses. */
                updateHouses();

                boolean existMayor = existMayor(new ArrayList<>(getPlayerList()));
                boolean existAlphaWolf = existAlphaWolf(new ArrayList<>(getPlayerList()));

                /* Start MAYOR or ALPHA_WOLF vote if they do not exist. */
                if (!existMayor) {
                    startVote(VoteType.MAYOR);
                }
                if (!existAlphaWolf) {
                    /* Only start alpha wolf vote if there isn't already one ongoing. */
                    if (getOngoingVotes().stream().noneMatch(vote -> {
                        try {
                            return vote.getVoteType() == VoteType.ALPHA_WOLF;
                        } catch (SQLException e) {
                            return true;
                        }
                    })) {
                        startVote(VoteType.ALPHA_WOLF);
                    }
                }

                /* Open day chat. */
                Chat.openChat(ChatManager.getInstance().getChatIDs(iid).stream()
                                .filter(chatIdentifier -> chatIdentifier.type() == ChatType.GENERAL).toList().get(0).id(),
                        true);
            }
            case VOTE -> {
                /* Start daily LYNCH vote. */
                startVote(VoteType.LYNCH);
                /* End MAYOR vote if it is ongoing. */
                endVote(VoteType.MAYOR);

                ActionManager.performActions(this.iid);
            }
            case EXECUTION -> {
                /* End daily LYNCH vote. */
                endVote(VoteType.LYNCH);
            }
            case EVENING -> {
                /* Close day chat. */
                Chat.openChat(ChatManager.getInstance().getChatIDs(iid).stream()
                                .filter(chatIdentifier -> chatIdentifier.type() == ChatType.GENERAL).toList().get(0).id(),
                        false);

                ActionManager.performActions(this.iid);
                /* Execute players in toBeExecuted list. */
                List<ActionDT> killActions = getKillActions();
                if (killActions.size() != 0 || getToBeExecuted().size() > 0) {
                    killMarkedPlayers(new PlayerIdentifier(this.iid, getGameMaster()),
                            new ArrayList<>(getToBeExecuted()), KillMarkedPlayers.LYNCH);
                }
                ActionManager.performActions(this.iid);
                /* Reset role actions. */
                replenishActions(new ArrayList<>(alivePlayers()), getInstanceState().getGameSpeed());

                /* Clear tied players list. */
                VoteDB.deleteTiedPlayers(this.iid);
            }
            case NIGHT -> {
                // Perform all actions in the buffer
                ActionManager.performActions(this.iid);
            }
        }

        /* See if there is a winning group already. */
        Group winner = getWinHandler().checkWin(alivePlayers());
        if (winner != null) {
            List<Integer> userIDs =
                    getPlayerList().stream().map(player -> player.getPlayerIdentifier().userID()).toList();

            /* Add win/loss to UserStats. */
            addWinOrLossPerRole(new ArrayList<>(getPlayerList()), winner);

            /* Notify winEvent. */
            WinEvent.getWinEvent().notify(iid, winner);

            /* Destroy Instance. */
            endGame();

            /* Delete GDPR entries. */
            UserDB.deleteUsersAfterInstanceEnd(userIDs);
        }

        /* Notify listeners that a phase change occurred. */
        NextPhaseEvent.getNextPhaseEvent().notify(this.iid);
    }

    private List<ActionDT> getKillActions() throws SQLException, ReflectiveOperationException {
        List<Integer> actions = ActionsDB.getActions(this.iid, "EXECUTED");
        List<ActionDT> killActions = new ArrayList<>();
        for (int actionId : actions) {
            ActionDT action = ActionsDB.getActionFromID(actionId);
            if (action.action() instanceof KillPlayers) {
                killActions.add(action);
                ActionsDB.completeAction(actionId);
            }
        }
        return killActions;
    }

    /**
     * Checks if the game has started already.
     */
    public boolean isStarted() throws SQLException {
        return getInstanceState() != null;
    }


    //////////   Voting related   //////////

    /**
     * Returns a list of ongoing, active votes
     *
     * @return ArrayList of ongoing votes
     */
    public List<Vote> getOngoingVotes() throws SQLException, NoSuchVoteException {
        return VoteRetriever.retrieveOngoingVotesByInstance(this.iid);
    }

    /**
     * Starts a vote
     *
     * @param voteType the type of vote that must be started.
     * @return Vote that has been created
     * @throws VotingException if something went wrong while starting a vote
     */
    public Vote startVote(VoteType voteType) throws SQLException, GameException, ReflectiveOperationException {
        if (getInstanceState().getDay() < 1) {
            throw new NoSuchVoteException("Cannot start vote as the game has not begun.");
        }

        List<Player> playerList = switch (voteType) {
            case LYNCH, MAYOR -> homeOwners();
            case ALPHA_WOLF -> alivePlayers().stream().filter(player -> {
                try {
                    return player.getMainRole().getGroup() == Group.WEREWOLVES;
                } catch (SQLException | ReflectiveOperationException | GameException e) {
                    e.printStackTrace();
                }
                return false;
            }).toList();
            default -> new ArrayList<>();
        };
        /* Create the vote in the DB, then retrieve the generated voteID. Then start it.*/
//        int vid = VoteDB.addNewVote(this.iid, voteType);
        Vote vote = switch (voteType) {
            case LYNCH, MAYOR -> new PlayerVote(this.iid, voteType, playerList);
            case ALPHA_WOLF -> new AlphaWolfVote(this.iid, playerList);
            default -> throw new NoSuchVoteException("Attempted to start a vote with an unspecified vote type");
        };
//        VoteDB.saveAllowedPlayers(vid, new HashSet<>(playerListToPlayerIdentifierList(playerList)));
//        VoteDB.modifyStarted(vid, true);
        vote.start();
        return vote;
    }

    /**
     * After voting has ended, this method will process the results
     *
     * @param results  complete list of players and the number of votes they received
     * @param voteType vote type, which determines what should happen to the chosen players //     * @return list of
     *                 chosen players
     */
    public void processVote(Map<PlayerIdentifier, Integer> results, VoteType voteType)
            throws SQLException, GameException, ReflectiveOperationException {
        if (results == null) {
            return;
        }

        /* The number of players that need to be chosen for this vote. */
        int AMOUNT_CHOSEN = voteResultSize(voteType);
        /* Sort the vote results in descending order. */
        LinkedList<PlayerIdentifier> sortedResults = sortMap(results);
        /* A list of selected players for this vote. The number of votes they received is maintained, hence it is a
        map. */
        LinkedList<PlayerIdentifier> selected = new LinkedList<>();
        /* Add the top players to the selected list. */
        int latestValue = 0;
        while (selected.size() < AMOUNT_CHOSEN && sortedResults.peek() != null) {
            PlayerIdentifier next = sortedResults.pop();
            selected.addFirst(next);
            latestValue = results.get(next);
        }
        /* Check whether we need the mayor to decide. */
        boolean needMayor = sortedResults.peek() != null && latestValue == results.get(sortedResults.peek()) &&
                voteType == VoteType.LYNCH;
        boolean needReElection = sortedResults.peek() != null && results.get(sortedResults.peek()) > 0 &&
                voteType == VoteType.ALPHA_WOLF;

        LinkedList<PlayerIdentifier> chosenPlayers = new LinkedList<>();

        chosenPlayers.addAll(selected);

        if (needMayor) {
            /* Find tied players. */
            int finalLatestValue = latestValue;
            List<PlayerIdentifier> tiedPlayers = new ArrayList<>();
            results.entrySet().stream().filter(entry -> entry.getValue() == finalLatestValue)
                    .forEach(entry -> tiedPlayers.add(entry.getKey()));

            // remove ties from chosenPlayers.
            int openSpots = 0;
            for (PlayerIdentifier playerIdentifier : tiedPlayers) {
                // Make a copy of identifiers
                if (chosenPlayers.stream()
                        .anyMatch(playerIdentifier1 -> playerIdentifier1.userID() == playerIdentifier.userID())) {
                    // Remove player from chosenPlayers (I swear this can be done cleaner)
                    chosenPlayers.remove(
                            chosenPlayers.stream().filter(player -> player.userID() == playerIdentifier.userID())
                                    .toList().get(0));
                    openSpots++;
                }
            }

            // Set Instance variables 'tiedPlayers' and 'undecidedLynches' to proper values.
            VoteDB.setTiedPlayers(tiedPlayers);
            VoteDB.setUndecidedLynchesByInstanceID(iid, openSpots);
        }

        /* Start a new ALPHA_WOLF vote if no unanimous first place is reached. */
        if (needReElection) {
            startVote(VoteType.ALPHA_WOLF);
            return;
        }

        /* Apply the results to the (remaining) chosenPlayers. */
        applyResults(this.iid, -1, new ArrayList<>(chosenPlayers), voteType);
    }

    /**
     * Sorts a map based on the values attached to the keys, in descending order
     *
     * @param map an unsorted map
     * @return a sorted map in descending order
     */
    private LinkedList<PlayerIdentifier> sortMap(Map<PlayerIdentifier, Integer> map) {
        LinkedList<PlayerIdentifier> sortedList = new LinkedList<>();
        map.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .forEachOrdered(x -> sortedList.addLast(x.getKey()));
        return sortedList;
    }

//    /**
//     * Sorts a map based on the values attached to the keys, in descending order
//     *
//     * @param map an unsorted map
//     * @return a sorted map in descending order
//     */
//    private LinkedHashMap<Player, Integer> sortMap(Map<Player, Integer> map) {
//        LinkedHashMap<Player, Integer> sortedMap = new LinkedHashMap<>();
//        map.entrySet()
//                .stream()
//                .sorted(Collections.reverseOrder(
//                        Map.Entry.comparingByValue())
//                ).forEachOrdered(
//                        x -> sortedMap.put(x.getKey(), x.getValue())
//                );
//        return sortedMap;
//    }

    /**
     * Determines the number of players that are chosen for a given vote type
     *
     * @param voteType type of vote.
     */
    public int voteResultSize(VoteType voteType) throws SQLException {
        if (voteType == VoteType.LYNCH) {
            return getInstanceState().getGameSpeed() + 1;
        }
        return 1;
    }

    /**
     * Finds an ongoing vote of given type and ends it.
     *
     * @param vote The vote to be ended
     * @throws NoSuchVoteException if no vote of type {@code voteType} exists.
     */
    public void endVote(Vote vote, VoteType voteType) throws GameException, SQLException, ReflectiveOperationException {
        // Validate that a vote has been found
        if (vote == null) {
            throw new NoSuchVoteException("Vote does not found.");
        }

        processVote(vote.end(), voteType);
    }

    /**
     * Ends a vote based on a given voteType.
     *
     * @param voteType Type of vote to be ended.
     * @throws NoSuchVoteException if vote does not exist.
     */
    public void endVote(VoteType voteType) throws GameException, SQLException, ReflectiveOperationException {
        for (Vote v : getOngoingVotes()) {
            if (v.getVoteType() == voteType) {
                endVote(v, voteType);
            }
        }
    }

    /**
     * Add victims to the toExecute list. Can be used by MAYOR
     *
     * @param victims players that are going to be executed
     */
    @Deprecated
    public void executePlayers(List<Player> victims) throws SQLException {
        // TODO: Add exceptions
        // Check for duplicates
        for (Player victim : victims) {
            addToBeExecuted(victim.getPlayerIdentifier());
        }
    }
    //////////   Location related   //////////

    /**
     * Returns all locations in the game, i.e. all Player houses and all bridges
     *
     * @return every {@code Location} in the game
     */
    public List<Integer> getLocations() throws SQLException {
        return LocationDB.getLocationsFromInstanceID(this.iid);
    }

    /**
     * Moves a player to a {@code newLocation}
     *
     * @param player      Player to be moved.
     * @param newLocation Location to move {@code player} to.
     * @throws NoSuchPlayerException   if {@code player} cannot be found.
     * @throws NoSuchLocationException if {@code newLocation} cannot be found.
     */
    public void movePlayer(Player player, Location newLocation) throws GameException, SQLException {
        // Check player, throw exception if not found
        if (!getPlayerList().stream().map(Player::getPlayerIdentifier).toList()
                .contains(player.getPlayerIdentifier())) {
            throw new NoSuchPlayerException(
                    "Player cannot be moved since the player cannot be found in this instance.");
        }
        // Check newLocation, throw exception if not found
        if (!getLocations().contains(newLocation.getId())) {
            throw new NoSuchLocationException("Player cannot be moved since the new location cannot be found.");
        }
        // Add player to newLocation
        newLocation.visitPlayer(player);
    }

    /**
     * Progresses the building of a burned house
     */
    public void updateHouses() throws SQLException, GameException {
        // Find Burned houses
        List<Player> homelessPlayers = homelessPlayers();

        // build()
        for (Player p : homelessPlayers) {
            (new House(p.getHouse())).build();
        }
    }

    /**
     * Puts each player back at their own house.
     */
    public void resetPlayerLocation() throws SQLException, GameException {
        for (Player player : alivePlayers()) {
            movePlayer(player, new House(player.getHouse()));
        }
    }

    public ActionManager getActionManager() {
        return new ActionManager();
    }
}

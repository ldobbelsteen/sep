package org.lukos.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.lukos.controller.request.*;
import org.lukos.controller.response.*;
import org.lukos.controller.websocket.GameEndEvent;
import org.lukos.controller.websocket.InstanceAction;
import org.lukos.controller.websocket.InstanceNotification;
import org.lukos.database.ActionMessagesDB;
import org.lukos.database.InstanceDB;
import org.lukos.database.UserDB;
import org.lukos.model.actionsystem.ActionEnc;
import org.lukos.model.actionsystem.actions.ActionMessageDT;
import org.lukos.model.chatsystem.*;
import org.lukos.model.events.NextPhaseEvent;
import org.lukos.model.events.WinEvent;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.NoPermissionException;
import org.lukos.model.exceptions.instances.GameAlreadyStartedException;
import org.lukos.model.exceptions.location.BridgeDoesNotExistException;
import org.lukos.model.exceptions.location.HouseDoesNotExistException;
import org.lukos.model.exceptions.user.NoSuchPlayerException;
import org.lukos.model.exceptions.user.NoSuchUserException;
import org.lukos.model.exceptions.user.UserException;
import org.lukos.model.instances.DayPhase;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.instances.InstanceState;
import org.lukos.model.listeners.NextPhaseListener;
import org.lukos.model.listeners.WinEventListener;
import org.lukos.model.location.Bridge;
import org.lukos.model.location.House;
import org.lukos.model.location.Location;
import org.lukos.model.rolesystem.DoubleRole;
import org.lukos.model.rolesystem.Group;
import org.lukos.model.rolesystem.Purpose;
import org.lukos.model.rolesystem.RoleActionInformation;
import org.lukos.model.user.IssuerSub;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.User;
import org.lukos.model.user.UserManager;
import org.lukos.model.user.player.Player;
import org.lukos.model.voting.Ballot;
import org.lukos.model.voting.Vote;
import org.lukos.model.voting.VoteRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.lukos.controller.util.InstanceHelper.getInstance;
import static org.lukos.controller.util.InstanceHelper.getInstanceWithPermissionsCheck;
import static org.lukos.controller.util.PlayerHelper.listPlayerRoles;
import static org.lukos.controller.util.PlayerHelper.playerInGame;
import static org.lukos.controller.util.UserHelper.*;

@RestController
@RequestMapping()
public class GeneralController implements ErrorController, NextPhaseListener, WinEventListener {

    private final SimpMessagingTemplate template;
    private final ChatManager chatManager = ChatManager.getInstance();

    @Autowired
    public GeneralController(SimpMessagingTemplate template) {
        this.template = template;
        NextPhaseEvent.getNextPhaseEvent().subscribe(this);
        WinEvent.getWinEvent().subscribe(this);
    }

    // <===== API Methods GameChatController =====>

    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "500", description = "processing error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping(value = "/api/game/{game_id}/chat/{chat_id}/submit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessResponse> submitChat(@PathVariable("game_id") int gid,
                                                      @PathVariable("chat_id") int cid,
                                                      @AuthenticationPrincipal OAuth2User principal,
                                                      @RequestBody String message) throws Exception {
        Instant time = Instant.now();

        User auth = getUser(principal);
        Player player = auth.getPlayer();

        if (!playerInGame(gid, player)) {
            throw new NoSuchPlayerException("Player is not in this game, and cannot submit chats.");
        }

        if (!InstanceManager.getInstanceManager().getInstance(gid).isStarted()) {
            throw new NoPermissionException("Chats aren't accessible when the game hasn't started yet");
        }

        int uid = player.getPlayerIdentifier().userID();

        boolean writeAccess = Chat.checkPermission(cid, uid, gid);
        if (!writeAccess) {
            throw new NoPermissionException("The player is not allowed to write messages to this chat");
        }

        Chat.submitChat(uid, cid, message, time);

        ChatMessage text = new ChatMessage(cid, new MessageEntry(auth.getUid(), time, message));
        this.template.convertAndSend("/topic/" + gid + "/chat/" + cid, text);
        return new ResponseEntity<>(new SuccessResponse("Chat was sent successfully"), HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "500", description = "processing error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/api/game/{game_id}/chat/{chat_id}/history")
    public ResponseEntity<MessageResponse> getHistory(@PathVariable("game_id") int gid,
                                                      @PathVariable("chat_id") int cid, @RequestParam int delta,
                                                      @RequestParam int amount,
                                                      @AuthenticationPrincipal OAuth2User principal) throws Exception {
        Instant time = Instant.now();
        time = time.minusSeconds(delta);

        User auth = getUser(principal);
        Player player = auth.getPlayer();

        if (!playerInGame(gid, player)) {
            throw new NoSuchPlayerException("Player is not in this game, thus cannot access chat history.");
        }

        if (!InstanceManager.getInstanceManager().getInstance(gid).isStarted()) {
            throw new NoPermissionException("Chats aren't accessible when the game hasn't started yet");
        }

        List<ChatMessage> messages = Chat.getMessages(cid, time, amount);

        return new ResponseEntity<>(new MessageResponse(messages), HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "500", description = "processing error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/api/game/{game_id}/chat/list")
    public ResponseEntity<ChatListResponse> listChats(@PathVariable("game_id") int gameId,
                                                      @AuthenticationPrincipal OAuth2User principal) throws Exception {
        User auth = getUser(principal);
        Player player = auth.getPlayer();

        if (!playerInGame(gameId, player)) {
            throw new NoSuchPlayerException("Player is not in this game");
        }

        if (!InstanceManager.getInstanceManager().getInstance(gameId).isStarted()) {
            throw new NoPermissionException("Chats aren't accessible when the game hasn't started yet");
        }

        int uid = player.getPlayerIdentifier().userID();

        List<ChatStatus> chats = chatManager.getPlayerChats(uid);

        return new ResponseEntity<>(new ChatListResponse(chats), HttpStatus.OK);
    }

    // <===== API Methods GameGeneralController =====>

    /**
     * Returns the current status of the game.
     *
     * @param gameId    the ID of the game
     * @param principal the principle of the request
     * @return the current status of the game
     * @throws NoSuchUserException if the user of the principal does not exist
     */
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "500", description = "processing error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/api/game/{game_id}/status")
    public ResponseEntity<SingleGameInformationResponse> getGameStatus(@PathVariable("game_id") String gameId,
                                                                       @AuthenticationPrincipal OAuth2User principal)
            throws GameException, SQLException {
        int iid = Integer.parseInt(gameId);
        IInstance instance = getInstanceWithPermissionsCheck(iid, principal);
        InstanceState state = instance.getInstanceState();

        if (instance.isStarted()) {
            return new ResponseEntity<>(new SingleGameInformationResponse(instance.getIid(), instance.getGameName(),
                    instance.getGameMaster(), instance.isStarted(), new GameEntry(state.getPhase(), state.getDay())),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(
                new SingleGameInformationResponse(instance.getIid(), instance.getGameName(), instance.getGameMaster(),
                        instance.isStarted(), new GameEntry(null, null)), HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "500", description = "processing error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/api/game/{game_id}/bridge")
    public ResponseEntity<BridgeListResponse> getBridgeList(@PathVariable("game_id") String gameId,
                                                            @AuthenticationPrincipal OAuth2User principal)
            throws SQLException, GameException {
        int iid = Integer.parseInt(gameId);
        IInstance instance = getInstanceWithPermissionsCheck(iid, principal);

        List<BridgeEntry> bridges = new ArrayList<>();
        for (int bridgeID : instance.getBridges()) {
            bridges.add(new BridgeEntry(bridgeID, (new Bridge(bridgeID)).getName()));
        }

        return new ResponseEntity<>(new BridgeListResponse(bridges), HttpStatus.OK);
    }

    // <===== API Methods GamePhaseController =====>

    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "500", description = "processing error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/api/game/{game_id}/phase/next")
    private ResponseEntity<SuccessResponse> nextPhase(@PathVariable("game_id") String gameId,
                                                      @AuthenticationPrincipal OAuth2User principal)
            throws GameException, SQLException, ReflectiveOperationException, MessagingException {
        getInstanceWithPermissionsCheck(Integer.parseInt(gameId), principal).nextPhase();
        return new ResponseEntity<>(new SuccessResponse(""), HttpStatus.OK);
    }

    // <===== API Methods GamePlayerController =====>

    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "500", description = "processing error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/api/game/{game_id}/player/list")
    public ResponseEntity<ListPlayersResponse> playerList(@PathVariable("game_id") String gid,
                                                          @AuthenticationPrincipal OAuth2User principal)
            throws GameException, SQLException, ReflectiveOperationException {
        IInstance instance = getInstanceWithPermissionsCheck(Integer.parseInt(gid), principal);

        // initialize empty list of players
        List<PlayerEntry> players = new ArrayList<>();

        // every ResponsePlayer consists of two parameters:
        // 1. the UUID of that Player
        // 2. the status (alive/deceased/not_started) of that player (not_started is to
        // indicate that the player is
        // in a game that has not started yet)
        List<Player> playerList = instance.getPlayerList();

        if (instance.isStarted()) {
            Player thisPlayer = getUser(principal).getPlayer();
            Group thisPlayerGroup = getGroupPlayer(thisPlayer);
            if (thisPlayerGroup == Group.TOWNSPEOPLE) {
                for (Player player : playerList) {
                    PlayerStatus status = getPlayerStatus(player);
                    players.add(new PlayerEntry(new SimplePlayerEntry(player.getPlayerIdentifier().userID(), status,
                            UserManager.getInstance().getUser(player.getPlayerIdentifier().userID()).getUsername()),
                            Group.TOWNSPEOPLE));
                }
            } else {
                for (Player player : playerList) {
                    PlayerStatus status = getPlayerStatus(player);
                    Group group = getGroupPlayer(player);
                    if (thisPlayerGroup == group) {
                        players.add(new PlayerEntry(new SimplePlayerEntry(player.getPlayerIdentifier().userID(), status,
                                UserManager.getInstance().getUser(player.getPlayerIdentifier().userID()).getUsername()),
                                thisPlayerGroup));
                    } else {
                        players.add(new PlayerEntry(new SimplePlayerEntry(player.getPlayerIdentifier().userID(), status,
                                UserManager.getInstance().getUser(player.getPlayerIdentifier().userID()).getUsername()),
                                Group.TOWNSPEOPLE));
                    }
                }
            }
        } else {
            for (Player player : playerList) {
                players.add(new PlayerEntry(new SimplePlayerEntry(player.getPlayerIdentifier().userID(), null,
                        UserManager.getInstance().getUser(player.getPlayerIdentifier().userID()).getUsername()), null));
            }
        }

        return new ResponseEntity<>(new ListPlayersResponse(players), HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "500", description = "processing error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping("/api/game/{game_id}/player/submit_deathnote")
    public ResponseEntity<SuccessResponse> submitDeathnote(
            @RequestBody PlayerSubmitDeathnoteRequest playerSubmitDeathnoteRequest, @PathVariable("game_id") String gid,
            @AuthenticationPrincipal OAuth2User principal) throws GameException, SQLException {
        int iid = Integer.parseInt(gid);
        Player player = getUser(principal).getPlayer();

        if (!playerInGame(iid, player)) {
            throw new NoPermissionException("User is not in a game!");
        }
        if (!player.getDeathnote().getChangeable()) {
            throw new NoPermissionException("User cannot submit a new deathnote because the player died in the game!");
        }
        if (!InstanceManager.getInstanceManager().getInstance(iid).isStarted()) {
            throw new NoPermissionException("The game hasn't started yet");
        }

        player.updateNote(playerSubmitDeathnoteRequest.getContent());

        return new ResponseEntity<>(new SuccessResponse("Success"), HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "500", description = "processing error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/api/game/{game_id}/player/{player_id}/obituary")
    public ResponseEntity<ObituaryEntry> obituaryEntry(@PathVariable("game_id") String gid,
                                                       @PathVariable("player_id") String pid,
                                                       @AuthenticationPrincipal OAuth2User principal)
            throws GameException, SQLException, ReflectiveOperationException {
        int iid = Integer.parseInt(gid);
        int uid = Integer.parseInt(pid);
        User requester = getUser(principal);
        Player player = getUser(uid).getPlayer();

        // Checks whether the user is actually in the game, and whether if a player is
        // alive it is that player that
        // requests the obituary, because you should not be able to see the obituary of
        // other alive players.
        if (!userInGame(iid, requester) || (player.alive() && requester.getUid() != uid)) {
            throw new NoPermissionException("You cannot perform this command!");
        }
        if (!InstanceManager.getInstanceManager().getInstance(iid).isStarted()) {
            throw new NoPermissionException("The game hasn't started yet");
        }

        return new ResponseEntity<>(new ObituaryEntry(listPlayerRoles(player), player.getDeathnote().getContent()),
                HttpStatus.OK);
    }

    // <===== API Methods GameRoleActionController =====>

    @PostMapping("/api/game/{game_id}/purpose/submit")
    public ResponseEntity<SuccessResponse> actionSubmit(@RequestBody ActionSubmitRequest actionSubmitRequest,
                                                        @PathVariable("game_id") String gameId,
                                                        @AuthenticationPrincipal OAuth2User principal)
            throws SQLException, GameException, ReflectiveOperationException {
        int instanceID = Integer.parseInt(gameId);
        IInstance instance = getInstanceWithPermissionsCheck(instanceID, principal);
        User user = getUser(principal);

        if (!instance.isStarted()) {
            throw new NoPermissionException("The game has not yet started");
        }

        List<Location> locations = new ArrayList<>();
        List<Integer> bridges = actionSubmitRequest.getBridgeIDs();
        // FIXME: Change Integer to int
        for (Integer bridgeID : instance.getBridges()) {
            if (bridges.stream().anyMatch(id -> id == bridgeID)) {
                locations.add(new Bridge(bridgeID));
            }
        }
        if (bridges.size() != locations.size()) {
            throw new BridgeDoesNotExistException("That bridge does not exist!");
        }
        for (int userID : actionSubmitRequest.getHouseIDs()) {
            Player player = getUser(userID).getPlayer();
            if (!playerInGame(instanceID, player)) {
                throw new HouseDoesNotExistException("That house does not exist!");
            }
            locations.add(new House(player.getHouse()));
        }

        List<Player> players = new ArrayList<>();
        for (int userID : actionSubmitRequest.getPlayerIDs()) {
            Player player = getUser(userID).getPlayer();
            if (!playerInGame(instanceID, player)) {
                throw new NoSuchPlayerException("That player does not exist!");
            }
            players.add(player);
        }

        user.getPlayer().performAction(new ActionEnc(locations.stream().map(Location::getId).toList(),
                players.stream().map(Player::getPlayerIdentifier).toList()), actionSubmitRequest.getAction());

        return new ResponseEntity<>(new SuccessResponse("You have successfully performed the action."), HttpStatus.OK);
    }

    @GetMapping("/api/game/{game_id}/purpose/action_information")
    public ResponseEntity<GameRoleActionResponse> actionInformation(@PathVariable("game_id") String gameId,
                                                                    @AuthenticationPrincipal OAuth2User principal)
            throws SQLException, GameException, ReflectiveOperationException {
        int instanceID = Integer.parseInt(gameId);
        User user = getUser(principal);

        if (!userInGame(instanceID, user)) {
            throw new NoPermissionException("You cannot perform this command!");
        }

        IInstance instance = getInstanceWithPermissionsCheck(instanceID, principal);
        Player player = user.getPlayer();
        List<RoleActionInformation> actionsInfo = new ArrayList<>();
        for (Purpose purpose : player.getPurposes()) {
            List<RoleActionInformation> roleAction = purpose.getInformation(instance, player.getPlayerIdentifier());
            if (!roleAction.isEmpty()) {
                actionsInfo.addAll(roleAction);
            }
        }

        return new ResponseEntity<>(new GameRoleActionResponse("", actionsInfo), HttpStatus.OK);
    }

    @GetMapping("/api/game/{game_id}/purpose/action_result")
    public ResponseEntity<ActionResultResponse> actionResult(@PathVariable("game_id") String gameId,
                                                             @AuthenticationPrincipal OAuth2User principal)
            throws SQLException, GameException {

        int instanceID = Integer.parseInt(gameId);
        User user = getUser(principal);

        if (!userInGame(instanceID, user)) {
            throw new NoPermissionException("You cannot perform this command!");
        }

        IInstance instance = getInstance(instanceID);

        if (!instance.isStarted()) {
            throw new NoPermissionException("The game has not yet started");
        }

        Player player = user.getPlayer();

        List<ActionMessageDT> messages = new ArrayList<>();

        // Get all message that need to be sent
        List<Integer> messageIds = ActionMessagesDB.getAllNotSendMessagesForUser(player.getPlayerIdentifier());
        for (int id : messageIds) {
            messages.add(ActionMessagesDB.getMessage(id));
        }

        return new ResponseEntity<>(new ActionResultResponse("Messages retrieved successfully", messages),
                HttpStatus.OK);
    }

    // <===== API Methods GameRoleController =====>

    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "500", description = "processing error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/api/game/{game_id}/role")
    public ResponseEntity<RoleResponse> getRole(@PathVariable("game_id") String gameId,
                                                @AuthenticationPrincipal OAuth2User principal)
            throws ReflectiveOperationException, GameException, NullPointerException, SQLException {
        Player player = getUser(principal).getPlayer();

        // permission check
        if (!playerInGame(Integer.parseInt(gameId), player)) {
            throw new NoPermissionException(
                    "User attempted to access DayPhase for a game they are not participating in.");
        }

        return new ResponseEntity<>(new RoleResponse(listPlayerRoles(player)), HttpStatus.OK);
    }

    // <===== API Methods GameUserController =====>

    /**
     * With this request, the frontend can get its browser's user id as well as the game(s) it's playing in.
     *
     * @param principal the current authorised user.
     * @return a map containing a pair (user id, game id).
     */
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "500", description = "processing error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/api/user/current")
    public ResponseEntity<CurrentUserResponse> getCurrent(@AuthenticationPrincipal OAuth2User principal)
            throws UserException, SQLException {
        User user = getUser(principal);
        Player player;
        try {
            player = user.getPlayer();
            return new ResponseEntity<>(new CurrentUserResponse(user.getIssuer(), user.getSub(), user.getUid(),
                    player.getPlayerIdentifier().instanceID(), user.getUsername()), HttpStatus.OK);
        } catch (NoSuchPlayerException | SQLException e) {
            // if user has not joined a game yet, then the list will be null
            return new ResponseEntity<>(
                    new CurrentUserResponse(user.getIssuer(), user.getSub(), user.getUid(), user.getUsername()),
                    HttpStatus.OK);
        }
    }

    /**
     * Remove the user's account and return a confirmation.
     *
     * @param principal is the user that should be removed.
     * @return confirmation of removal.
     */
    @ApiResponses(value = {@ApiResponse(responseCode = "501", description = "Not implemented",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping("/api/user/gdpr/delete_account")
    public ResponseEntity<SuccessResponse> deleteAccount(@AuthenticationPrincipal OAuth2User principal)
            throws UserException, SQLException {
        // TODO: sql.deleteData()...
        User user = getUser(principal);
        IssuerSub inp = new IssuerSub(user.getIssuer(), user.getSub());
        setToRemoveUser(inp); // this sets the user to be removed after the instance completes

        SuccessResponse response = new SuccessResponse("User removed successfully");

        // TODO: Send http request to /api/logout to trigger logout
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Request a user info change by providing data slot and new data.
     *
     * @param principal is the user whose info is edited.
     * @param update    contains the data key and the new data.
     * @return whether the update was successful.
     */
    @ApiResponses(value = {@ApiResponse(responseCode = "501", description = "Not implemented",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping("/api/user/gdpr/update_user_info")
    public ResponseEntity<SuccessResponse> updateUserInfo(@AuthenticationPrincipal OAuth2User principal,
                                                          @RequestBody SubmitProfileUpdate update)
            throws UserException, SQLException {
        User user = getUser(principal);
        if (update.getDataKey() == ProfileData.USERNAME) {
            user.setUsername(update.getData());
        }

        SuccessResponse response = new SuccessResponse("Username was successfully updated");

        try {
            Player player = user.getPlayer();
            if (player == null) {
                throw new NoSuchPlayerException("Player does not exist");
            }
            this.template.convertAndSend("/topic/" + player.getPlayerIdentifier().instanceID() + "/users", response);
        } catch (SQLException | GameException e) {
            response = new SuccessResponse("Username was successfully updated, but user is not in a game.");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Download one's user info in a logical format.
     *
     * @param principal is the user whose data will be downloaded.
     * @return whether providing the download was successful.
     */
    @ApiResponses(value = {@ApiResponse(responseCode = "501", description = "Not implemented",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping(value = "/api/user/gdpr/download_user_info", produces = "text/csv")
    public ResponseEntity<?> downloadUserInfo(@AuthenticationPrincipal OAuth2User principal) {
        try {
            User user = getUser(principal);
            File file = UserDB.getAllUserInfo(user.getUid()); // = getReportService().generateReport(reportName);

            return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=userData.csv")
                    .contentLength(file.length()).contentType(MediaType.parseMediaType("text/csv"))
                    .body(new FileSystemResource(file));

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to generate report: userData",
                    e);
        }
    }

    // <===== API Methods GameVoteController =====>

    /**
     * Returns the ongoing votes for the game with ID {@code gid}.
     *
     * @param gid The game ID
     * @return A list with ongoing votes
     */
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "500", description = "processing error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/api/game/{game_id}/vote")
    public ResponseEntity<GetVoteResponse> getVotes(@PathVariable("game_id") String gid,
                                                    @AuthenticationPrincipal OAuth2User principal)
            throws GameException, SQLException {
        List<VoteEntry> voteEntries = new ArrayList<>();

        int iid = Integer.parseInt(gid);

        Player player = getUser(principal).getPlayer();

        if (!playerInGame(iid, player)) {
            throw new NoPermissionException("Player is not in the game");
        }

        // the current game instance
        IInstance instance = getInstance(Integer.parseInt(gid)); // FIXME make robust against faulty UUID inputs
        PlayerIdentifier playerID = player.getPlayerIdentifier();

        // a list of the ongoing votes in the instance
        List<Vote> gameVotes = instance.getOngoingVotes();

        for (Vote vote : gameVotes) {
            if (vote.getAllowed().contains(playerID) && vote.isBusy()) {
                voteEntries.add(new VoteEntry(vote.getVid(), vote.getVoteType()));
            }
        }

        return new ResponseEntity<>(new GetVoteResponse(voteEntries), HttpStatus.OK);
    }

    /**
     * Submits a vote for a user to the game with a target and returns whether the action was successful.
     *
     * @param gid       The game ID
     * @param vid       The vote ID // TODO: Fix javadoc. // * @param tid The target ID
     * @param principal The user principal
     * @return A message whether the vote was successful
     */
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "500", description = "processing error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping(value = "/api/game/{game_id}/vote/{vote_id}/submit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessResponse> submitVote(@PathVariable("game_id") String gid,
                                                      @PathVariable("vote_id") String vid,
                                                      @RequestBody VoteSubmitRequest voteSubmitRequest,
                                                      @AuthenticationPrincipal OAuth2User principal)
            throws GameException, NullPointerException, SQLException, ReflectiveOperationException {
        int tid = voteSubmitRequest.getTargetID();
        int iid = Integer.parseInt(gid);
        int voteID = Integer.parseInt(vid);
        Player player = getUser(principal).getPlayer();
        Player target = getUser(tid).getPlayer();

        if (!playerInGame(iid, player)) {
            throw new NullPointerException("User is not in a game!");
        }
        if (!playerInGame(iid, target)) {
            throw new NullPointerException("Voted user is not in the game!");
        }
        if (!InstanceManager.getInstanceManager().getInstance(iid).isStarted()) {
            throw new NoPermissionException("Can't vote because the game hasn't started yet");
        }

        player.vote(voteID, target);

        return new ResponseEntity<>(new SuccessResponse("Vote on " + tid + " was successful!"), HttpStatus.OK);
    }

    /**
     * Returns all the players that are eligible to vote in the vote with vid as vote ID.
     *
     * @param gid       The game ID
     * @param vid       The vote ID
     * @param principal The user principal
     * @return The players that are eligible to vote
     */
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "500", description = "processing error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/api/game/{game_id}/vote/{vote_id}/eligible")
    public ResponseEntity<GetVoteEligibleResponse> getEligible(@PathVariable("game_id") String gid,
                                                               @PathVariable("vote_id") String vid,
                                                               @AuthenticationPrincipal OAuth2User principal)
            throws GameException, SQLException {
        Vote vote = getVote(principal, gid, vid);

        List<SimplePlayerEntry> eligible = new ArrayList<>();
        for (PlayerIdentifier player : vote.getAllowed()) {
            String username = UserManager.getInstance().getUser(player.userID()).getUsername();
            if (InstanceDB.getAlivePlayers(Integer.parseInt(gid)).contains((player))) {
                eligible.add(new SimplePlayerEntry(player.userID(), PlayerStatus.ALIVE, username));
            } else {
                // player is deceased
                // assumes players are either alive or dead
                eligible.add(new SimplePlayerEntry(player.userID(), PlayerStatus.DECEASED, username));
            }

        }
        return new ResponseEntity<>(new GetVoteEligibleResponse(eligible), HttpStatus.OK);
    }

    /**
     * Returns all ballots that are cast from the vote with vid as vote ID.
     *
     * @param gid       The game ID
     * @param vid       The vote ID
     * @param principal The user principal
     * @return The ballots from the vote
     */
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "500", description = "processing error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/api/game/{game_id}/vote/{vote_id}/ballots")
    public ResponseEntity<GetVoteBallotResponse> getBallots(@PathVariable("game_id") String gid,
                                                            @PathVariable("vote_id") String vid,
                                                            @AuthenticationPrincipal OAuth2User principal)
            throws SQLException, GameException {
        // TODO: Check permission
        Vote vote = getVote(principal, gid, vid);

        // list in which to store entries of the output
        List<BallotEntry> ballots = new ArrayList<>();

        // list of cast ballots to add to the list in the output
        List<Ballot> voteBallots = vote.getBallots();

        for (Ballot voteBallot : voteBallots) {
            ballots.add(new BallotEntry(voteBallot.player().userID(), voteBallot.target().userID()));
        }

        return new ResponseEntity<>(new GetVoteBallotResponse(ballots), HttpStatus.OK);
    }

    // <===== API Methods InstanceController =====>

    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "500", description = "processing error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping("/api/lobby/start")
    public ResponseEntity<SuccessResponse> startGame(@AuthenticationPrincipal OAuth2User principal)
            throws GameException, NullPointerException, SQLException, ReflectiveOperationException {
        User user = getUser(principal);
        int uid = user.getUid();

        Player player = user.getPlayer();
        if (player == null) {
            throw new NullPointerException("user is not in a game");
        }

        int iid = player.getPlayerIdentifier().instanceID();
        if (getInstance(iid).isStarted()) {
            throw new GameAlreadyStartedException("You cannot start a game that has already started.");
        }
        getInstance(iid).startGame(uid);

        InstanceNotification text = new InstanceNotification(uid, InstanceAction.START);
        this.template.convertAndSend("/topic/lobby/" + iid, text);

        return new ResponseEntity<>(new SuccessResponse("game started successfully"), HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "500", description = "processing error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping(value = "/api/lobby/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SingleGameResponse> createGame(@RequestBody GameCreateRequest gameCreateRequest,
                                                         @AuthenticationPrincipal OAuth2User principal)
            throws SQLException, GameException {
        User user;

        user = getUser(principal);

        // create the actual game
        user.createGame(gameCreateRequest.getGameName(), gameCreateRequest.getSEED());

        return new ResponseEntity<>(new SingleGameResponse(user.getPlayer().getPlayerIdentifier().instanceID()),
                HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "500", description = "processing error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping("/api/lobby/stop")
    public ResponseEntity<SuccessResponse> stopGame(@AuthenticationPrincipal OAuth2User principal)
            throws GameException, SQLException {
        User user = getUser(principal);
        Player player = user.getPlayer();

        if (player == null) {
            throw new NullPointerException("User is not in a lobby!");
        }

        int iid = player.getPlayerIdentifier().instanceID();
        IInstance instance = InstanceManager.getInstanceManager().getInstance(iid);

        if (instance.getGameMaster() != user.getUid()) {
            throw new NoPermissionException("You are not the game master and hence cannot stop this game!");
        }

        instance.stopGame();

        int uid = user.getUid();
        InstanceNotification text = new InstanceNotification(uid, InstanceAction.CANCEL);
        this.template.convertAndSend("/topic/lobby/" + iid, text);

        return new ResponseEntity<>(new SuccessResponse("Success"), HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "500", description = "processing error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "missing parameter gameID",
                    content = @Content(schema = @Schema(implementation = Object.class)))})
    @PostMapping(value = "/api/lobby/join", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SingleGameResponse> joinGame(@RequestBody GameJoinRequest gameJoinRequest,
                                                       @AuthenticationPrincipal OAuth2User principal)
            throws GameException, NullPointerException, SQLException {
        User user = getUser(principal);
        int iid = gameJoinRequest.getJoinCode();

        if (!InstanceManager.getInstanceManager().getInstance(iid).isStarted()) {
            user.joinGame(iid);
        } else {
            throw new GameAlreadyStartedException("You cannot join a game that has already started");
        }

        int uid = user.getUid();
        InstanceNotification text = new InstanceNotification(uid, InstanceAction.JOIN);
        this.template.convertAndSend("/topic/lobby/" + iid, text);

        return new ResponseEntity<>(new SingleGameResponse(iid), HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "500", description = "processing error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "missing parameter gameID",
                    content = @Content(schema = @Schema(implementation = Object.class)))})
    @PostMapping("/api/lobby/leave")
    public ResponseEntity<SuccessResponse> leaveGame(@AuthenticationPrincipal OAuth2User principal)
            throws GameException, NullPointerException, SQLException {
        User user = getUser(principal);
        Player player = user.getPlayer();

        if (player == null) {
            throw new NullPointerException("User is not in a lobby!");
        }

        int iid = player.getPlayerIdentifier().instanceID();

        if (InstanceManager.getInstanceManager().getInstance(iid).getGameMaster() == user.getUid()) {
            throw new NoPermissionException("You cannot leave a game you created, you have to stop it!");
        }

        int uid = user.getUid();
        InstanceNotification text = new InstanceNotification(uid, InstanceAction.LEAVE);
        this.template.convertAndSend("/topic/lobby/" + iid, text);

        user.leaveGame(iid);

        return new ResponseEntity<>(new SuccessResponse("Success"), HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "500", description = "processing error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping("/api/lobby/gamethrow")
    public ResponseEntity<SuccessResponse> throwGame(@AuthenticationPrincipal OAuth2User principal)
            throws GameException, NullPointerException, SQLException, ReflectiveOperationException {
        User user = getUser(principal);

        Player player = user.getPlayer();
        if (player == null) {
            throw new NullPointerException("user is not in a game");
        }

        int iid = player.getPlayerIdentifier().instanceID();
        int uid = user.getUid();

        if (InstanceManager.getInstanceManager().getInstance(iid).getGameMaster() != user.getUid()) {
            throw new NoPermissionException("You cannot throw the game because you are not the creator!");
        }
        /* Throw the game by killing all werewolves. */
        getInstance(iid).throwGame(uid);

        return new ResponseEntity<>(new SuccessResponse("game is thrown. next phase town wins."), HttpStatus.OK);
    }

    // <===== API Methods UnauthorizedController =====>

    /**
     * Errorhandler for the API.
     *
     * @param request the actual request that caused the error.
     * @return an HTTP error if HTTP status not found or internal, just "error" otherwise.
     */
    @RequestMapping("/error")
    public ResponseEntity<ErrorResponse> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return new ResponseEntity<>(new ErrorResponse("error-404"), HttpStatus.NOT_FOUND);
            } else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
                return new ResponseEntity<>(new ErrorResponse("error-401"), HttpStatus.UNAUTHORIZED);
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return new ResponseEntity<>(new ErrorResponse("error-403"), HttpStatus.FORBIDDEN);
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return new ResponseEntity<>(new ErrorResponse("error-500"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(new ErrorResponse("error-unknown"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // <===== Helper methods GamePhaseController =====>

    public void nextPhase(int gid) throws GameException, SQLException, MessagingException {
        DayPhase text = getInstance(gid).getInstanceState().getPhase();
        this.template.convertAndSend("/topic/" + gid + "/phase", text);
    }

    @Override
    public void nextPhaseUpdate(int gid) throws SQLException, GameException {
        this.nextPhase(gid);
    }

    // <===== Helper methods GamePlayerController =====>

    private static PlayerStatus getPlayerStatus(Player player) throws SQLException, GameException {
        if (player.alive()) {
            return PlayerStatus.ALIVE;
        }
        return PlayerStatus.DECEASED;
    }

    private static Group getGroupPlayer(Player player)
            throws GameException, ReflectiveOperationException, SQLException {
        List<DoubleRole> doubleRoles = player.getDoubleRoles();
        if (doubleRoles.size() > 0) {
            return doubleRoles.get(0).getGroup();
        }
        return player.getMainRole().getGroup();
    }

    // <===== Helper methods GameVoteController =====>

    // Helper method to get a vote from a game and vote id
    private Vote getVote(OAuth2User principal, String gid, String vid)
            throws NullPointerException, GameException, SQLException {
        User user = getUser(principal);

        int iid = Integer.parseInt(gid);
        int voteID = Integer.parseInt(vid);
        IInstance instance = getInstance(iid);
        Vote vote = VoteRetriever.retrieveVoteByIdIfInInstance(voteID, instance.getIid());

        // if user is not in game or if user is not in list of players that are allowed
        // to vote
        if (!userInGame(iid, user) || !vote.getAllowed().contains(user.getPlayer().getPlayerIdentifier())) {
            throw new NoPermissionException("User is not allowed to request for ballots.");
        }
        return vote;
    }

    // <===== Helper methods InstanceController =====>

    @Override
    public void win(int gid, Group winGroup) throws GameException, SQLException, ReflectiveOperationException {
        List<Player> playerList = getInstance(gid).getPlayerList();
        List<Integer> usernames = new ArrayList<>();

        for (Player player : playerList) {
            if (player.getMainRole().getGroup() == winGroup) {
                // add the list to the response
                usernames.add(player.getPlayerIdentifier().userID());
            }
        }

        GameEndEvent text = new GameEndEvent(winGroup, usernames);
        this.template.convertAndSend("/topic/" + gid + "/end", text);
    }
}

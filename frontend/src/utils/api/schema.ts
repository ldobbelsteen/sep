import { z } from "zod";
import { toCamelCase, toSnakeCase } from "../tools";

/**
 * JSON schemas for validating data sent to and received from the API. Uses Zod
 * as validation library to make sure data is present and formed correctly.
 */
export default class Schema {
  /** Check if data is valid according to a schema */
  static isValid = <T, U>(data: T, schema: z.Schema<T, z.ZodTypeDef, U>) => {
    return schema.safeParse(data).success;
  };

  /** General format for identifiers */
  static IdSchema = z.number().int().nonnegative();

  /** String with at least one character */
  static NonEmptyString = z.string().min(1);

  /** Function which preprocesses data by converting it to camel case */
  static CamelCasePreprocess = (s: unknown) => {
    return s && typeof s === "string" ? toCamelCase(s) : s;
  };

  /** Function which postprocesses data back into snake case for the backend */
  static SnakeCasePostProcess = (s: unknown) => {
    return s && typeof s === "string" ? toSnakeCase(s) : s;
  };

  /** All success schemas should extend this */
  static BaseSuccess = z.object({
    message: z.string().nullable().optional(),
  });

  /** All error schemas should extend this */
  static BaseError = z.object({
    error: this.NonEmptyString.nullable(),
  });

  /**
   * Schema which represents a logged in user. Includes data available from the
   * oauth provider the user logged in with. Includes the game ID if the user is
   * currently part of a game instance.
   */
  static User = this.BaseSuccess.extend({
    id: this.IdSchema,
    name: this.NonEmptyString,
    gameId: z
      .array(this.IdSchema)
      .transform((arr) => (arr.length > 0 ? arr[0] : undefined)),
  });

  /** Schema for updating the data of a user */
  static UpdateUserInfo = z.object({
    dataKey: z.enum(["USERNAME"]),
    data: this.NonEmptyString,
  });

  /** Schema for configuration required for creating a new lobby */
  static LobbyConfig = z.object({
    gameName: this.NonEmptyString,
    seed: z.number().int().nonnegative(),
  });

  /** Schema for data required for joining a lobby */
  static LobbyJoin = z.object({
    joinCode: this.IdSchema,
  });

  /** Schema for lobby update broadcast info */
  static LobbyUpdate = z.object({
    uid: this.IdSchema,
    action: z.preprocess(
      this.CamelCasePreprocess,
      z.enum(["join", "leave", "start", "cancel"])
    ),
  });

  /** Schema which is returned upon creating or joining a lobby */
  static NewLobby = this.BaseSuccess.extend({
    gameId: this.IdSchema,
  });

  /** List of all possible phases */
  static PhaseList = [
    "morning",
    "day",
    "vote",
    "execution",
    "evening",
    "night",
  ] as const;

  /** Schema which represents all possible game phases */
  static Phase = z.enum(this.PhaseList);

  /**
   * Schema which represents the current state of a game. Represents a lobby if
   * the lobby hasn't been started yet.
   */
  static GameInfo = this.BaseSuccess.extend({
    gameId: this.IdSchema,
    gameName: this.NonEmptyString,
    gameMaster: this.IdSchema,
    started: z.boolean(),
    gameEntry: z.object({
      dayPhase: z.preprocess(this.CamelCasePreprocess, this.Phase.optional()),
      day: z.number().int().nonnegative().optional(),
    }),
  }).transform((game) => ({
    message: game.message,
    id: game.gameId,
    name: game.gameName,
    gamemaster: game.gameMaster,
    hasStarted: game.started,
    phase: game.gameEntry.dayPhase,
    day: game.gameEntry.day,
  }));

  /** Schema which represents all possible player statuses */
  static Status = z.enum(["alive", "deceased"]);

  /** List of all possible role names */
  static RoleList = [
    "alphaWolf",
    "archer",
    "clairvoyant",
    "graverobber",
    "guardianAngel",
    "healer",
    "mayor",
    "medium",
    "poisoner",
    "privateInvestigator",
    "werewolf",
  ] as const;

  /** Schema which represents all possible role names */
  static Role = z.enum(this.RoleList);

  /** List of all possible role group names */
  static GroupList = ["townspeople", "werewolves", "nonwinning"] as const;

  /** Schema which represents all possible role group names */
  static Group = z.enum(this.GroupList);

  /** Schema which represents a player in a simple way */
  static SimplePlayerEntry = z
    .object({
      id: this.IdSchema,
      name: this.NonEmptyString,
      playerStatus: z.preprocess(
        this.CamelCasePreprocess,
        this.Status.optional()
      ),
    })
    .transform((player) => ({
      id: player.id,
      name: player.name,
      status: player.playerStatus,
    }));

  /** Schema containing general info on a player */
  static Player = z
    .object({
      simplePlayerEntry: this.SimplePlayerEntry,
      group: z.preprocess(
        this.CamelCasePreprocess,
        this.Group.optional().nullable()
      ),
    })
    .transform((player) => ({
      entry: player.simplePlayerEntry,
      group: player.group,
    }));

  /** Schema which represents the list of players currently in a game. */
  static PlayerList = this.BaseSuccess.extend({
    players: z.array(this.Player),
  });

  /** Schema which represents the list of bridges and their names */
  static Bridges = this.BaseSuccess.extend({
    bridgeNames: z.array(
      z.object({
        id: this.IdSchema,
        name: this.NonEmptyString,
      })
    ),
  });

  /** Schema for submitting a player's new deathnote */
  static Deathnote = z.object({
    content: this.NonEmptyString,
  });

  /** Schema which represents a role by its name and the group it belongs to */
  static PlayerRole = z.object({
    name: z.preprocess(this.CamelCasePreprocess, this.Role),
    group: z.preprocess(
      this.CamelCasePreprocess,
      this.Group.optional().nullable()
    ),
  });

  /** Schema which represents the roles of a player */
  static PlayerRoles = this.BaseSuccess.extend({
    playerRoles: z.array(this.PlayerRole),
  });

  /** Schema for an obituary entry of a player */
  static Obituary = this.BaseSuccess.extend({
    roles: z.array(this.PlayerRole),
    deathNote: this.NonEmptyString.nullable(),
  });

  /** List of all possible vote types */
  static VoteTypeList = ["lynch", "mayor", "alphaWolf", "misc"] as const;

  /** Schema which represents all possible vote types */
  static VoteType = z.enum(this.VoteTypeList);

  /** Schema which represents all of a game's running votes */
  static RunningVotes = this.BaseSuccess.extend({
    voteEntries: z.array(
      z.object({
        id: this.IdSchema,
        voteType: z.preprocess(this.CamelCasePreprocess, this.VoteType),
      })
    ),
  });

  /** Schema which represents the eligible players of a vote */
  static Eligible = this.BaseSuccess.extend({
    eligible: z.array(this.SimplePlayerEntry),
  });

  /** Schema which represents the submitted ballots of a vote */
  static Ballots = this.BaseSuccess.extend({
    ballotEntries: z.array(
      z.object({
        player: this.IdSchema,
        target: this.IdSchema,
      })
    ),
  });

  /** Schema which represents the target of something */
  static Target = z.object({
    targetID: this.IdSchema,
  });

  /** List of all possible chat types */
  static ChatTypeList = ["general", "deceased", "wolves"] as const;

  /** Schema which represents all of the possible chat types */
  static ChatType = z.enum(this.ChatTypeList);

  /** Schema which represents a single chat */
  static Chat = z.object({
    id: this.IdSchema,
    type: z.preprocess(this.CamelCasePreprocess, this.ChatType),
    isOpen: z.boolean(),
  });

  /** Schema which represents all of the available chats */
  static ChatList = this.BaseSuccess.extend({
    chats: z.array(this.Chat),
  });

  /** Schema which represents a single historical message */
  static ChatMessage = z.object({
    chatId: this.IdSchema,
    message: z.object({
      id: this.IdSchema,
      timestamp: z.preprocess((t) => {
        if (typeof t === "string") return new Date(t);
      }, z.date()),
      content: this.NonEmptyString,
    }),
  });

  /** Schema which represents the history of a chat */
  static ChatHistory = this.BaseSuccess.extend({
    history: z.array(this.ChatMessage),
  });

  /** List of all possible action types */
  static ActionTypeList = [
    "alphaWolfKill",
    "burn",
    "clairvoyantSeeRole",
    "clean",
    "heal",
    "kill",
    "mayorDecide",
    "poison",
    "privateInvestigate",
    "protect",
    "revive",
    "robGrave",
    "shoot",
    "soak",
    "successorAlphaWolf",
    "successorMayor",
  ] as const;

  /** Schema which represents all of the possible action types */
  static ActionType = z.enum(this.ActionTypeList);

  /** Schema for submitting an action choice */
  static ActionSubmit = z.object({
    action: z.preprocess(this.SnakeCasePostProcess, z.string()),
    playerIDs: z.array(this.IdSchema),
    houseIDs: z.array(this.IdSchema),
    bridgeIDs: z.array(this.IdSchema),
  });

  /** Schema for currently available actions */
  static ActionList = this.BaseSuccess.extend({
    actions: z.array(
      z.object({
        action: z.preprocess(this.CamelCasePreprocess, this.ActionType),
        eligible: z.object({
          PLAYER: z.array(this.IdSchema).optional(),
          HOUSE: z.array(this.IdSchema).optional(),
          BRIDGE: z.array(this.IdSchema).optional(),
        }),
        numberOfVotes: z.number().int().nonnegative(),
      })
    ),
  });

  /** List of all possible action message types */
  static ActionMessageTypeList = [
    "changedRoleGlobalMessage",
    "changedRoleMessage",
    "healedPlayerMessage",
    "lynchKillBroadcastMessage",
    "mayorDecideMessage",
    "moveToBridgeMessage",
    "moveToHouseMessage",
    "newMayorMessage",
    "nightKillBroadcastMessage",
    "protectPlayerMessage",
    "revivePlayersMessage",
    "seeCharacterMessage",
    "seeRoleMessage",
    "youHaveBeenRevivedMessage",
  ] as const;

  /** Schema which represents all of the possible action message types */
  static ActionMessageType = z.enum(this.ActionMessageTypeList);

  /** Schema which represents the result message tied to a single action */
  static ActionMessage = z.object({
    messageType: z.preprocess(this.CamelCasePreprocess, this.ActionMessageType),
    data: z.array(z.string()),
  });

  /** Schema which represents a list of action messages */
  static ActionMessageList = this.BaseSuccess.extend({
    actions: z.array(this.ActionMessage),
  });

  /** Schema for a game end event */
  static EndEvent = z.object({
    winGroup: z.preprocess(this.CamelCasePreprocess, this.Group),
    userIDs: z.array(this.IdSchema),
  });
}

import Cookies from "js-cookie";
import { z } from "zod";
import Schema from "./schema";
import { ActionType, UserInfoData } from "./types";

/** Function which allows sleep in async/await functions */
const sleep = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

/** The number of milliseconds to delay requests */
const requestDelay = 0;

/**
 * Helper class which assists in sending GET and POST requests to the HTTP API.
 * Forces validation of data sent to and received from the API.
 */
export default class HTTP {
  /** Check if an error union is an error */
  static isError = <T, U extends z.infer<typeof Schema.BaseError>>(
    obj: T | U
  ): obj is U => (obj as U).error !== undefined;

  /**
   * Send a GET request to an endpoint. The data returned will be validated
   * against either the success or error schema depending on the response
   * status. The returned promise is rejected only if there is a schema
   * mismatch. Returns types are derived from the schemas.
   */
  private static getRequest = async <
    T extends z.infer<typeof Schema.BaseSuccess>,
    U extends z.infer<typeof Schema.BaseSuccess>,
    V extends z.infer<typeof Schema.BaseError>,
    W extends z.infer<typeof Schema.BaseError>
  >(
    url: string,
    successSchema: z.Schema<T, z.ZodTypeDef, U>,
    errorSchema: z.Schema<V, z.ZodTypeDef, W>
  ): Promise<T | V> => {
    if (requestDelay) await sleep(requestDelay);
    const response = await fetch(url, {
      method: "GET",
    });
    return this.handleResponse(response, successSchema, errorSchema);
  };

  /**
   * Send a POST request to an endpoint. The data to be posted is first
   * validated against its schema. The data returned will be validated against
   * either the success or error schema depending on the response status. The
   * returned promise is rejected only if there is a schema mismatch. Returns
   * types are derived from the schemas.
   */
  private static postRequest = async <
    T,
    U,
    V extends z.infer<typeof Schema.BaseSuccess>,
    W extends z.infer<typeof Schema.BaseSuccess>,
    X extends z.infer<typeof Schema.BaseError>,
    Y extends z.infer<typeof Schema.BaseError>
  >(
    url: string,
    data: T,
    dataSchema: z.Schema<T, z.ZodTypeDef, U>,
    successSchema: z.Schema<V, z.ZodTypeDef, W>,
    errorSchema: z.Schema<X, z.ZodTypeDef, Y>
  ): Promise<V | X> => {
    const dataResult = dataSchema.safeParse(data);
    if (dataResult.success) {
      if (requestDelay) await sleep(requestDelay);
      const response = await fetch(url, {
        method: "POST",
        headers: {
          "X-XSRF-TOKEN": Cookies.get("XSRF-TOKEN") ?? "",
          "Content-Type": "application/json",
        },
        body: JSON.stringify(dataResult.data),
      });
      return this.handleResponse(response, successSchema, errorSchema);
    } else {
      return Promise.reject(dataResult.error);
    }
  };

  /**
   * Handle validating the response of a normal HTTP API request. If the
   * response is OK, it is validated against the success schema and else it is
   * validated against the error schema. The promise only rejects if there is a
   * schema mismatch.
   */
  private static handleResponse = async <
    A extends z.infer<typeof Schema.BaseSuccess>,
    B extends z.infer<typeof Schema.BaseSuccess>,
    C extends z.infer<typeof Schema.BaseError>,
    D extends z.infer<typeof Schema.BaseError>
  >(
    response: Response,
    successSchema: z.Schema<A, z.ZodTypeDef, B>,
    errorSchema: z.Schema<C, z.ZodTypeDef, D>
  ) => {
    const json = await response.json(); // eslint-disable-line @typescript-eslint/no-unsafe-assignment
    if (response.ok) {
      const successResult = successSchema.safeParse(json);
      if (successResult.success) {
        return successResult.data;
      } else {
        return Promise.reject(successResult.error);
      }
    } else {
      const errorResult = errorSchema.safeParse(json);
      if (errorResult.success) {
        return errorResult.data;
      } else {
        return Promise.reject(errorResult.error);
      }
    }
  };

  /** Logout the currently logged in user. Returns whether it was succesful or not. */
  static logout = async () => {
    const response = await fetch("/api/logout", {
      method: "POST",
      headers: {
        "X-XSRF-TOKEN": Cookies.get("XSRF-TOKEN") ?? "",
      },
    });
    return response.ok;
  };

  /** Endpoints in the user directive */
  static User = class extends this {
    /** Get info on the currently logged in user */
    static current = () => {
      const url = "/api/user/current";
      return this.getRequest(url, Schema.User, Schema.BaseError);
    };

    /** Endpoints in the GDPR directive */
    static GDPR = class extends this {
      /** Update user info */
      static updateUserInfo = (data: UserInfoData) => {
        const url = "/api/user/gdpr/update_user_info";
        return this.postRequest(
          url,
          data,
          Schema.UpdateUserInfo,
          Schema.BaseSuccess,
          Schema.BaseError
        );
      };

      /** Download user info */
      static downloadUserInfo = "/api/user/gdpr/download_user_info";

      /** Delete account */
      static deleteAccount = () => {
        const url = "/api/user/gdpr/delete_account";
        return this.postRequest(
          url,
          undefined,
          z.undefined(),
          Schema.BaseSuccess,
          Schema.BaseError
        );
      };
    };
  };

  /** Endpoints in the lobby directive */
  static Lobby = class extends this {
    /** Create a new lobby with a configuration */
    static create = (config: z.infer<typeof Schema.LobbyConfig>) => {
      const url = "/api/lobby/create";
      void config;
      return this.postRequest(
        url,
        config,
        Schema.LobbyConfig,
        Schema.NewLobby,
        Schema.BaseError
      );
    };

    /** Join an existing lobby using an invite code */
    static join = (config: z.infer<typeof Schema.LobbyJoin>) => {
      const url = "/api/lobby/join";
      return this.postRequest(
        url,
        config,
        Schema.LobbyJoin,
        Schema.NewLobby,
        Schema.BaseError
      );
    };

    /** Start a lobby that hasn't been started yet. Only available to the gamemaster. */
    static start = () => {
      const url = "/api/lobby/start";
      return this.postRequest(
        url,
        undefined,
        z.undefined(),
        Schema.BaseSuccess,
        Schema.BaseError
      );
    };

    /** Leave a lobby as a non-gamemaster player */
    static leave = () => {
      const url = "/api/lobby/leave";
      return this.postRequest(
        url,
        undefined,
        z.undefined(),
        Schema.BaseSuccess,
        Schema.BaseError
      );
    };

    /** Delete the lobby as the gamemaster */
    static stop = () => {
      const url = "/api/lobby/stop";
      return this.postRequest(
        url,
        undefined,
        z.undefined(),
        Schema.BaseSuccess,
        Schema.BaseError
      );
    };

    /** Throw a game instance */
    static throw = () => {
      const url = "/api/lobby/gamethrow";
      return this.postRequest(
        url,
        undefined,
        z.undefined(),
        Schema.BaseSuccess,
        Schema.BaseError
      );
    };
  };

  /** Endpoints in the game directive */
  static Game = class extends this {
    /** Get general status info on a game */
    static status = (gameId: number) => {
      const url = "/api/game/" + gameId.toString() + "/status";
      return this.getRequest(url, Schema.GameInfo, Schema.BaseError);
    };

    /** Get bridge names */
    static bridges = (gameId: number) => {
      const url = "/api/game/" + gameId.toString() + "/bridge";
      return this.getRequest(url, Schema.Bridges, Schema.BaseError);
    };

    /** Endpoints in the player directive */
    static Player = class extends this {
      /** List the players that are participating in the game */
      static list = (gameId: number) => {
        const url = "/api/game/" + gameId.toString() + "/player/list";
        return this.getRequest(url, Schema.PlayerList, Schema.BaseError);
      };

      /** Submit a (new) deathnote for the current player */
      static submitDeathnote = (gameId: number, content: string) => {
        const url =
          "/api/game/" + gameId.toString() + "/player/submit_deathnote";
        return this.postRequest(
          url,
          { content },
          Schema.Deathnote,
          Schema.BaseSuccess,
          Schema.BaseError
        );
      };

      /** Get a specific player's obituary when they have died */
      static obituary = (gameId: number, playerId: number) => {
        const url =
          "/api/game/" +
          gameId.toString() +
          "/player/" +
          playerId.toString() +
          "/obituary";
        return this.getRequest(url, Schema.Obituary, Schema.BaseError);
      };
    };

    /** Endpoints in role directive */
    static Role = class extends this {
      /** Get the current user's role(s) in the game */
      static get = (gameId: number) => {
        const url = "/api/game/" + gameId.toString() + "/role";
        return this.getRequest(url, Schema.PlayerRoles, Schema.BaseError);
      };
    };

    /** Endpoints in the phase directive */
    static Phase = class extends this {
      /** Progress the game to the next game manually */
      static next = (gameId: number) => {
        const url = "/api/game/" + gameId.toString() + "/phase/next";
        return this.getRequest(url, Schema.BaseSuccess, Schema.BaseError);
      };
    };

    /** Endpoints in the vote directive */
    static Vote = class extends this {
      /** Get the votes currently running in the game */
      static list = (gameId: number) => {
        const url = "/api/game/" + gameId.toString() + "/vote";
        return this.getRequest(url, Schema.RunningVotes, Schema.BaseError);
      };

      /** Get the list of players which are eligible for a vote */
      static eligible = (gameId: number, voteId: number) => {
        const url =
          "/api/game/" +
          gameId.toString() +
          "/vote/" +
          voteId.toString() +
          "/eligible";
        return this.getRequest(url, Schema.Eligible, Schema.BaseError);
      };

      /** Get the list of currently submitted ballots for a vote */
      static ballots = (gameId: number, voteId: number) => {
        const url =
          "/api/game/" +
          gameId.toString() +
          "/vote/" +
          voteId.toString() +
          "/ballots";
        return this.getRequest(url, Schema.Ballots, Schema.BaseError);
      };

      /** Submit a vote on a specific player for a specific vote */
      static submit = (gameId: number, voteId: number, targetId: number) => {
        const url =
          "/api/game/" +
          gameId.toString() +
          "/vote/" +
          voteId.toString() +
          "/submit";
        return this.postRequest(
          url,
          { targetID: targetId },
          Schema.Target,
          Schema.BaseSuccess,
          Schema.BaseError
        );
      };
    };

    /** Endpoints in the chat directive */
    static Chat = class extends this {
      /** Get the list of available chats */
      static list = (gameId: number) => {
        const url = "/api/game/" + gameId.toString() + "/chat/list";
        return this.getRequest(url, Schema.ChatList, Schema.BaseError);
      };

      /** Get the chat history of a chat */
      static history = (
        gameId: number,
        chatId: number,
        maxDelta: number,
        maxAmount: number
      ) => {
        const url =
          "/api/game/" +
          gameId.toString() +
          "/chat/" +
          chatId.toString() +
          "/history?delta=" +
          maxDelta.toString() +
          "&amount=" +
          maxAmount.toString();
        return this.getRequest(url, Schema.ChatHistory, Schema.BaseError);
      };

      /** Submit a message in a specific chat */
      static submit = (gameId: number, chatId: number, message: string) => {
        const url =
          "/api/game/" +
          gameId.toString() +
          "/chat/" +
          chatId.toString() +
          "/submit";
        return this.postRequest(
          url,
          message,
          Schema.NonEmptyString,
          Schema.BaseSuccess,
          Schema.BaseError
        );
      };
    };

    /** Endpoints in purpose directive */
    static Purpose = class extends this {
      /** Get the currently available actions */
      static actions = (gameId: number) => {
        const url =
          "/api/game/" + gameId.toString() + "/purpose/action_information";
        return this.getRequest(url, Schema.ActionList, Schema.BaseError);
      };

      /** Submit the choice of an action */
      static submit = (
        gameId: number,
        action: ActionType,
        playerIds: number[],
        houseIds: number[],
        bridgeIds: number[]
      ) => {
        const url = "/api/game/" + gameId.toString() + "/purpose/submit";
        return this.postRequest(
          url,
          {
            action: action,
            playerIDs: playerIds,
            houseIDs: houseIds,
            bridgeIDs: bridgeIds,
          },
          Schema.ActionSubmit,
          Schema.BaseSuccess,
          Schema.BaseError
        );
      };

      /** Get executed action results */
      static messages = (gameId: number) => {
        const url = "/api/game/" + gameId.toString() + "/purpose/action_result";
        return this.getRequest(url, Schema.ActionMessageList, Schema.BaseError);
      };
    };
  };
}

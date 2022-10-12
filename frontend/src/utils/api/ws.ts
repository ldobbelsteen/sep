import { Client, IMessage } from "@stomp/stompjs";
import Cookies from "js-cookie";
import { z } from "zod";
import Schema from "./schema";

/**
 * Helper class which creates a single connection to the WebSocket backend.
 * Helps with subscribing to topics and parsing responses.
 */
export default class WS {
  client: Client;
  gameId: number;

  /**
   * Async 'constructor' which creates a connection. Creates an unsecured
   * WebSocket connection when the application is served over HTTP and a secured
   * one if served over HTTPS.
   */
  static async connect(gameId: number): Promise<WS> {
    const prefix = window.location.protocol === "https:" ? "wss:" : "ws:";
    const url = prefix + "//" + window.location.host + "/api/socket";

    return await new Promise((resolve, reject) => {
      const client = new Client({
        brokerURL: url,
        connectHeaders: { "X-XSRF-TOKEN": Cookies.get("XSRF-TOKEN") ?? "" },
      });

      client.onConnect = () => {
        resolve(new WS(client, gameId));
      };

      client.onStompError = (receipt) => {
        reject(receipt);
      };

      client.activate();
    });
  }

  /** Non-public constructor because async constructors are not directly possible. */
  private constructor(client: Client, gameId: number) {
    this.client = client;
    this.gameId = gameId;
  }

  /**
   * Generic function to subscribe to a WebSocket endpoint. Handles parsing
   * received data in the supplied schema. Outputs the data to a callback if the
   * data is valid, or ouptuts the error to the other callback if the parsing
   * failed. Returns a function which will unsubscribe again.
   */
  private subscribe = <T, U>(
    url: string,
    dataSchema: z.Schema<T, z.ZodTypeDef, U>,
    successCallback: (data: T) => void,
    errorCallback: (err: z.ZodError<U>) => void
  ) => {
    const subscription = this.client.subscribe(url, (msg: IMessage) => {
      const result = dataSchema.safeParse(JSON.parse(msg.body));
      if (result.success) {
        successCallback(result.data);
      } else {
        errorCallback(result.error);
      }
    });
    return () => subscription.unsubscribe();
  };

  /** Subscribe to chat message broadcasts */
  subscribeToChat = (
    chatId: number,
    output: (msg: z.infer<typeof Schema.ChatMessage>) => void,
    error: (err: z.ZodError<z.infer<typeof Schema.ChatMessage>>) => void
  ) => {
    const url =
      "/topic/" + this.gameId.toString() + "/chat/" + chatId.toString();
    return this.subscribe(url, Schema.ChatMessage, output, error);
  };

  /** Subscribe to phase change broadcasts */
  subscribeToPhase = (
    output: (msg: z.infer<typeof Schema.Phase>) => void,
    error: (err: z.ZodError<z.infer<typeof Schema.Phase>>) => void
  ) => {
    const url = "/topic/" + this.gameId.toString() + "/phase";
    return this.subscribe(
      url,
      z.preprocess(Schema.CamelCasePreprocess, Schema.Phase),
      output,
      error
    );
  };

  /** Subscribe to user change broadcasts */
  subscribeToUser = (
    output: (msg: z.infer<typeof Schema.BaseSuccess>) => void,
    error: (err: z.ZodError<z.infer<typeof Schema.BaseSuccess>>) => void
  ) => {
    const url = "/topic/" + this.gameId.toString() + "/users";
    return this.subscribe(url, Schema.BaseSuccess, output, error);
  };

  /** Subscribe to lobby change broadcasts */
  subscribeToLobby = (
    output: (msg: z.infer<typeof Schema.LobbyUpdate>) => void,
    error: (err: z.ZodError<z.infer<typeof Schema.LobbyUpdate>>) => void
  ) => {
    const url = "/topic/lobby/" + this.gameId.toString();
    return this.subscribe(url, Schema.LobbyUpdate, output, error);
  };

  /** Subscribe to game end events */
  subscribeToEnd = (
    output: (msg: z.infer<typeof Schema.EndEvent>) => void,
    error: (err: z.ZodError<z.infer<typeof Schema.EndEvent>>) => void
  ) => {
    const url = "/topic/" + this.gameId.toString() + "/end";
    return this.subscribe(url, Schema.EndEvent, output, error);
  };
}

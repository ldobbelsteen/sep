import { z } from "zod";
import Schema from "./schema";

/**
 * Types inferred from several of the schemas specified in the schemas file.
 * This serves as a way of getting the type of the result of a schema in a more
 * concise way rather than inferring the type separately everywhere.
 */
export type LobbyConfig = z.infer<typeof Schema.LobbyConfig>;
export type User = z.infer<typeof Schema.User>;
export type UserInfoData = z.infer<typeof Schema.UpdateUserInfo>;
export type GameInfo = z.infer<typeof Schema.GameInfo>;
export type PlayerRole = z.infer<typeof Schema.PlayerRole>;
export type Bridges = z.infer<typeof Schema.Bridges>;
export type Player = z.infer<typeof Schema.Player>;
export type SimplePlayerEntry = z.infer<typeof Schema.SimplePlayerEntry>;
export type Ballots = z.infer<typeof Schema.Ballots>;
export type Eligible = z.infer<typeof Schema.Eligible>;
export type RunningVotes = z.infer<typeof Schema.RunningVotes>;
export type Chat = z.infer<typeof Schema.Chat>;
export type ChatList = z.infer<typeof Schema.ChatList>;
export type ChatMessage = z.infer<typeof Schema.ChatMessage>;
export type ChatHistory = z.infer<typeof Schema.ChatHistory>;
export type Obituary = z.infer<typeof Schema.Obituary>;
export type ActionType = z.infer<typeof Schema.ActionType>;
export type ActionList = z.infer<typeof Schema.ActionList>;
export type ActionMessage = z.infer<typeof Schema.ActionMessage>;

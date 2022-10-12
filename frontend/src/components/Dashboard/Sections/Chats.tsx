import React, { useCallback, useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import HTTP from "../../../utils/api/http";
import {
  ChatList,
  GameInfo,
  ChatHistory,
  ChatMessage,
  Player,
  Chat,
} from "../../../utils/api/types";
import WS from "../../../utils/api/ws";
import { readableTime } from "../../../utils/tools";
import { ToastErrorAny, TranslatorContext } from "../../Misc/Helpers";
import { Select } from "../../Misc/Select";

/**
 * Simple chat screen allowing users to switch between chats, see the chat
 * history and submit chat messages themselves.
 */
export const Chats = (props: {
  user: Player;
  game: GameInfo;
  players: Player[];
  socket: WS;
}) => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  const [chats, setChats] = useState<ChatList>();
  const [history, setHistory] = useState<Record<number, ChatHistory>>({});
  const [selectedChat, setSelectedChat] = useState<Chat | null>(null);
  const [messageInput, setMessageInput] = useState("");

  /**
   * Get the list of available chats on first load, phase change and player list
   * change. This will make sure that things stay in sync, e.g. when the user
   * dies they get access to the deceased chat.
   */
  useEffect(() => {
    HTTP.Game.Chat.list(props.game.id)
      .then((res) => {
        if (HTTP.isError(res)) {
          toast.error(res.error);
        } else {
          setChats(res);
        }
        return null;
      })
      .catch(ToastErrorAny);
  }, [props.game.id, props.game.phase, props.players]);

  /**
   * If there's no selected chat and there are chats in the state, select a
   * default chat. The default chat is the first in the array of chats, which is
   * the general channel.
   */
  useEffect(() => {
    if (!selectedChat && chats && chats.chats.length !== 0) {
      setSelectedChat(chats.chats[0]);
    }
  }, [chats, selectedChat]);

  /** Get chat history once we know the chat IDs */
  useEffect(() => {
    if (chats) {
      chats.chats.forEach((chat) => {
        HTTP.Game.Chat.history(props.game.id, chat.id, 86400, 100)
          .then((res) => {
            if (HTTP.isError(res)) {
              toast.error(res.error);
            } else {
              res.history.sort(
                (a, b) =>
                  b.message.timestamp.getTime() - a.message.timestamp.getTime()
              );
              setHistory((h) => {
                return { ...h, [chat.id]: res };
              });
            }
            return null;
          })
          .catch(ToastErrorAny);
      });
    }
  }, [props.game.id, chats]);

  /** Append a message to the history of a specific chat */
  const appendMessage = useCallback((chatId: number, message: ChatMessage) => {
    setHistory((h) => {
      return {
        ...h,
        [chatId]: {
          ...h[chatId],
          history: [message, ...h[chatId].history],
        },
      };
    });
  }, []);

  /** Subscribe to chat updates */
  useEffect(() => {
    if (chats && props.socket) {
      const unsubscribes: (() => void)[] = [];
      for (const chat of chats.chats) {
        unsubscribes.push(
          props.socket.subscribeToChat(
            chat.id,
            (msg) => appendMessage(chat.id, msg),
            ToastErrorAny
          )
        );
      }
      return () => {
        unsubscribes.forEach((u) => u());
      };
    }
  }, [props.socket, props.game.id, chats, appendMessage]);

  /** Handle submitting a new message to the API. */
  const handleSend = (ev: React.FormEvent<HTMLFormElement>) => {
    ev.preventDefault();
    if (selectedChat && messageInput) {
      setMessageInput("");
      HTTP.Game.Chat.submit(props.game.id, selectedChat.id, messageInput)
        .then((res) => {
          if (HTTP.isError(res)) {
            toast.error(res.error);
          }
          return null;
        })
        .catch(ToastErrorAny);
    }
  };

  /** Check whether the currently selected chat is open */
  const selectedIsOpen =
    chats && chats.chats.find((c) => c.id === selectedChat?.id)?.isOpen;

  /** Render the whole chat screen consisting of some subcomponents */
  return (
    <section className="column-center-children gap">
      {/**
       * Render the header for selecting chat channels. Translates the chat channel
       * type to the current language.
       */}
      <div className="row-center-children gap">
        <h2>{keyTranslator("chat")}</h2>
        {chats && (
          <Select
            multiSelect={false}
            value={selectedChat ?? undefined}
            options={chats.chats}
            onChange={(option) => setSelectedChat(option?.value ?? null)}
            display={(v) => keyTranslator(v.type)}
          />
        )}
      </div>
      {/**
       * Main box which contains the history of chat channels. Shows messages in
       * chronological order. It shows the user who sent the message, what they sent,
       * and when they did so.
       */}
      <div className="column-center-children gap full-size">
        <div className="subsection scrollbox">
          <div className="reverse-order">
            {!selectedChat ||
            !history[selectedChat.id] ||
            history[selectedChat.id].history.length === 0 ? (
              <span className="greyed-out">{keyTranslator("noMessages")}</span>
            ) : (
              history[selectedChat.id].history.map((msg, index) => (
                <span className="float-outward" key={index}>
                  <span className="left">
                    <strong>
                      {(() => {
                        const player = props.players.find(
                          (p) => p.entry.id === msg.message.id
                        );
                        if (!player) return keyTranslator("unknown");
                        if (props.user.entry.id === player.entry.id) {
                          return (
                            player.entry.name +
                            " (" +
                            keyTranslator("you", false) +
                            ")"
                          );
                        } else {
                          return player.entry.name;
                        }
                      })()}
                    </strong>
                    : {msg.message.content}
                  </span>
                  <span className="right">
                    {readableTime(msg.message.timestamp)}
                  </span>
                </span>
              ))
            )}
          </div>
        </div>
      </div>
      {/**
       * Chat input section. Shows an HTML input where the user can enter messages and
       * a submission button which will send the currently filled in message. Renders
       * as a form such that pressing enter will also send the message.
       */}
      <form className="full-size row-center-children gap" onSubmit={handleSend}>
        <input
          style={{ margin: 0, flexGrow: 1 }}
          placeholder={
            !selectedIsOpen
              ? keyTranslator("chatClosed")
              : keyTranslator("typeMessage")
          }
          value={messageInput}
          disabled={!selectedIsOpen}
          onChange={(ev) => {
            setMessageInput(ev.target.value);
          }}
        />
        <button type="submit" style={{ margin: 0 }} disabled={!selectedIsOpen}>
          {keyTranslator("send")}
        </button>
      </form>
    </section>
  );
};

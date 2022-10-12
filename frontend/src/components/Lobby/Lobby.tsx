import React, { useCallback, useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import HTTP from "../../utils/api/http";
import { GameInfo, Player } from "../../utils/api/types";
import WS from "../../utils/api/ws";
import { ClickToCopy } from "../Misc/ClickToCopy";
import { ToastErrorAny, TranslatorContext } from "../Misc/Helpers";
import { LoadingAnimation } from "../Misc/Loading";
import { LobbyButtons } from "./LobbyButtons";

/**
 * Interface that is shown while a lobby hasn't been started yet. Shows the
 * currently joined players and an invite code. The gamemaster will have a
 * button with which the lobby can be started and stopped.
 */
export const Lobby = (props: {
  userId: number;
  userName: string;
  game: GameInfo;
  notifyCancel: () => void;
  notifyStart: () => void;
  socket: WS;
}) => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  const [players, setPlayers] = useState<Player[] | null>(null);

  /** Callback for fetching the list of players */
  const updatePlayers = useCallback(() => {
    HTTP.Game.Player.list(props.game.id)
      .then((res) => {
        if (HTTP.isError(res)) {
          toast.error(res.error);
        } else {
          res.players.sort((a, b) => a.entry.name.localeCompare(b.entry.name));
          setPlayers(res.players);
        }
        return null;
      })
      .catch(ToastErrorAny);
  }, [props.game.id]);

  /** Get player list on first load */
  useEffect(updatePlayers, [updatePlayers]);

  /**
   * Subscribe to lobby updates. Notifies of lobby start or cancellation to the
   * parent component. Updates players in every other case, which are cases when
   * a player has joined or left the lobby.
   */
  useEffect(() => {
    return props.socket.subscribeToLobby((msg) => {
      switch (msg.action) {
        case "cancel":
          props.notifyCancel();
          break;
        case "start":
          props.notifyStart();
          break;
        case "join":
          updatePlayers();
          break;
        case "leave":
          /**
           * If it is the user themselves that left, don't update the list of
           * players. This would cause an error as the user would not be in the
           * game anymore.
           */
          if (msg.uid !== props.userId) {
            updatePlayers();
          }
          break;
      }
    }, ToastErrorAny);
  }, [props, updatePlayers]);

  /** Show loading animation while fetching player list for the first time. */
  if (!players) {
    return <LoadingAnimation />;
  }

  return (
    <div className="full-size column-center-children">
      <h2>{keyTranslator("lobby")}</h2>
      <section className="column-center-children gap">
        <span>
          <strong>{keyTranslator("name")}:</strong> {props.game.name}
        </span>
        <span>
          <strong>{keyTranslator("code")}:</strong>{" "}
          <ClickToCopy text={props.game.id.toString()} />
        </span>
      </section>
      <section className="column-center-children gap">
        <h3>{keyTranslator("players")}</h3>
        <div className="subsection scrollbox">
          <div className="column-center-children">
            {players.map((player, index) => (
              <span key={index}>
                {player.entry.name +
                  (props.userId === player.entry.id
                    ? " (" + keyTranslator("you", false) + ")"
                    : "")}
              </span>
            ))}
          </div>
        </div>
      </section>
      {/**
       * Render the stop, leave, start, logout and profile buttons based on whether
       * the user is the gamemaster.
       */}
      <LobbyButtons
        userName={props.userName}
        isGamemaster={props.game.gamemaster === props.userId}
        notifyCancel={props.notifyCancel}
      />
    </div>
  );
};

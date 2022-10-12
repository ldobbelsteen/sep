import React, { useCallback, useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import HTTP from "../../utils/api/http";
import { GameInfo, Player, PlayerRole } from "../../utils/api/types";
import WS from "../../utils/api/ws";
import { ToastErrorAny, TranslatorContext } from "../Misc/Helpers";
import { LoadingAnimation } from "../Misc/Loading";
import { Masonry } from "./Masonry";

/**
 * The dashboard represents a currently running game. This component
 * specifically handles fetching and updating the game state data. The sections
 * child component will handle rendering the interface.
 */
export const Dashboard = (props: {
  userId: number;
  game: GameInfo;
  socket: WS;
  notifyClose: () => void;
}) => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  const [userRoles, setUserRoles] = useState<PlayerRole[]>();
  const [players, setPlayers] = useState<Player[]>();
  const [user, setUser] = useState<Player>();

  /** Get player's roles on first load or phase change */
  useEffect(() => {
    HTTP.Game.Role.get(props.game.id)
      .then((res) => {
        if (HTTP.isError(res)) {
          toast.error(res.error);
        } else {
          setUserRoles(res.playerRoles);
        }
        return null;
      })
      .catch(ToastErrorAny);
  }, [props.game.id, props.game.phase]);

  /** Callback for fetching the list of players */
  const updatePlayers = useCallback(() => {
    HTTP.Game.Player.list(props.game.id)
      .then((res) => {
        if (HTTP.isError(res)) {
          toast.error(res.error);
        } else {
          res.players.sort((a, b) => a.entry.name.localeCompare(b.entry.name));
          setPlayers(res.players);
          setUser(res.players.find((p) => p.entry.id === props.userId));
        }
        return null;
      })
      .catch(ToastErrorAny);
  }, [props.game.id, props.userId]);

  /** Subscribe to user updates and update player list on broadcast */
  useEffect(() => {
    return props.socket.subscribeToUser(updatePlayers, ToastErrorAny);
  }, [props.socket, updatePlayers]);

  /** Get player list on first load or phase change */
  useEffect(updatePlayers, [updatePlayers, props.game.phase]);

  /** Subscribe to game end events */
  const notifyClose = props.notifyClose;
  useEffect(() => {
    return props.socket.subscribeToEnd((end) => {
      toast.success(
        keyTranslator("the") +
          " " +
          keyTranslator(end.winGroup, false) +
          " " +
          keyTranslator("haveWon", false)
      );
      notifyClose();
    }, ToastErrorAny);
  }, [props.socket, notifyClose, keyTranslator]);

  /** Don't render if not everything has been fetched yet. */
  if (!user || !userRoles || !players) {
    return <LoadingAnimation />;
  }

  /** Render a masonry of sections */
  return (
    <Masonry
      game={props.game}
      user={user}
      userRoles={userRoles}
      players={players}
      socket={props.socket}
    />
  );
};

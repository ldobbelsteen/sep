import React, { useCallback, useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import HTTP from "../utils/api/http";
import { User, GameInfo } from "../utils/api/types";
import WS from "../utils/api/ws";
import { Dashboard } from "./Dashboard/Dashboard";
import { Landing } from "./Lobby/Landing";
import { Lobby } from "./Lobby/Lobby";
import { ToastErrorAny } from "./Misc/Helpers";
import { LoadingAnimation } from "./Misc/Loading";

/**
 * Main game interface handler which assumes the user has been logged in. Shows
 * either lobby related components or the main game dashboard depending on the
 * user's game status.
 */
export const Game = (props: {
  user: User;
  setGameId: (id: number | undefined) => void;
}) => {
  const [game, setGame] = useState<GameInfo>();
  const [socket, setSocket] = useState<WS>();

  /** Open socket on first load */
  useEffect(() => {
    if (props.user.gameId) {
      WS.connect(props.user.gameId)
        .then((socket) => {
          setSocket(socket);
          return null;
        })
        .catch(ToastErrorAny);
    } else {
      setSocket(undefined);
    }
  }, [props.user.gameId]);

  /** Callback for fetching the game status */
  const updateGame = useCallback(() => {
    if (props.user.gameId) {
      HTTP.Game.status(props.user.gameId)
        .then((res) => {
          if (HTTP.isError(res)) {
            toast.error(res.error);
          } else {
            setGame(res);
          }
          return null;
        })
        .catch(ToastErrorAny);
    } else {
      setGame(undefined);
    }
  }, [props.user.gameId]);

  /** Subscribe to phase updates and update game status on broadcast */
  useEffect(() => {
    if (socket) {
      return socket.subscribeToPhase(updateGame, ToastErrorAny);
    }
  }, [socket, updateGame]);

  /** Update status after joining/creating a game */
  useEffect(updateGame, [updateGame]);

  /** If the user is not in a game, give the user the choice to join or create a lobby */
  if (!props.user.gameId) {
    return (
      <Landing
        userId={props.user.id}
        userName={props.user.name}
        setGameId={props.setGameId}
      />
    );
  }

  /**
   * If there is no status while the user is in a game, the status is being
   * loaded, so show a loading animation.
   */
  if (!game || !socket) {
    return <LoadingAnimation />;
  }

  /**
   * If the game hasn't started, show an interface where the currently joined
   * players can be viewed and the lobby can be started/stopped/left.
   */
  if (!game.hasStarted) {
    return (
      <Lobby
        userId={props.user.id}
        userName={props.user.name}
        game={game}
        notifyCancel={() => props.setGameId(undefined)}
        notifyStart={() => updateGame()}
        socket={socket}
      />
    );
  }

  /** If the game has started, show the main interface */
  return (
    <Dashboard
      userId={props.user.id}
      game={game}
      socket={socket}
      notifyClose={() => props.setGameId(undefined)}
    />
  );
};

import React from "react";
import { toast } from "react-hot-toast";
import HTTP from "../../utils/api/http";
import { ToastErrorAny, TranslatorContext } from "../Misc/Helpers";
import { Logout } from "../Misc/Logout";
import { Profile } from "../Profile/Profile";

/**
 * Render the logout and profile buttons unconditionally. Render the stop and
 * start buttons if the user is the lobby's gamemaster. Show the leave button
 * when the player is not gamemaster and can thus freely leave the game.
 */
export const LobbyButtons = (props: {
  userName: string;
  isGamemaster: boolean;
  notifyCancel: () => void;
}) => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  /** Start the lobby */
  const start = () => {
    HTTP.Lobby.start()
      .then((res) => {
        if (HTTP.isError(res)) {
          toast.error(res.error);
        }
        return null;
      })
      .catch(ToastErrorAny);
  };

  /**
   * Cancel the lobby. Notify of the cancellation to the parent component such
   * that it can act upon it.
   */
  const stop = () => {
    HTTP.Lobby.stop()
      .then((res) => {
        if (HTTP.isError(res)) {
          toast.error(res.error);
        } else {
          props.notifyCancel();
        }
        return null;
      })
      .catch(ToastErrorAny);
  };

  /**
   * Leave the lobby. Notify of the cancellation to the parent component such
   * that it can act upon it.
   */
  const leave = () => {
    HTTP.Lobby.leave()
      .then((res) => {
        if (HTTP.isError(res)) {
          toast.error(res.error);
        } else {
          props.notifyCancel();
        }
        return null;
      })
      .catch(ToastErrorAny);
  };

  return (
    <div className="row-center-children">
      <Logout />
      <Profile loggedIn={true} currentUsername={props.userName} />
      {props.isGamemaster && (
        <button onClick={stop}>{keyTranslator("stopGame")}</button>
      )}
      {!props.isGamemaster && (
        <button onClick={leave}>{keyTranslator("leaveGame")}</button>
      )}
      {props.isGamemaster && (
        <button onClick={start}>{keyTranslator("startGame")}</button>
      )}
    </div>
  );
};

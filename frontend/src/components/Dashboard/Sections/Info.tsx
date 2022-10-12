import React from "react";
import { toast } from "react-hot-toast";
import HTTP from "../../../utils/api/http";
import { Player, PlayerRole, GameInfo } from "../../../utils/api/types";
import { ClickToCopy } from "../../Misc/ClickToCopy";
import { ToastErrorAny, TranslatorContext } from "../../Misc/Helpers";
import { Time } from "../../Misc/Time";

/**
 * Show general game info and the user's player's general info. Includes game
 * name, game day, day phase, the users role and to which group that role
 * belongs and the status of the player (alive or deceased).
 */
export const Info = (props: {
  user: Player;
  userRoles: PlayerRole[];
  game: GameInfo;
}) => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  return (
    <section className="column-center-children gap">
      <h2>{keyTranslator("info")}</h2>
      {/**
       * Show the game name and copy the game's ID to the user's clipboard when they
       * click on it.
       */}
      <span>
        <strong>{keyTranslator("gameName")}: </strong>
        <ClickToCopy text={props.game.name} copy={props.game.id.toString()} />
      </span>
      {/**
       * Show the current in-game day and the time of day, which is in sync with the
       * system's clock.
       */}
      <span>
        <strong>
          {keyTranslator("day")} {props.game.day}
        </strong>{" "}
        - <Time />
      </span>
      {/** Show the game's current phase */}
      <span>
        <strong>{keyTranslator("phase")}: </strong>
        {keyTranslator(props.game.phase ?? "unkown")}
      </span>
      {/**
       * Show the roles of the user. If there are more than one, they are concatenated
       * with commas.
       */}
      <span>
        <strong>{keyTranslator("role")}: </strong>
        {props.userRoles
          .map((role, i) => keyTranslator(role.name, i === 0))
          .join(", ")}
      </span>
      {/** Show the group the user belongs to */}
      <span>
        <strong>{keyTranslator("group")}: </strong>
        {keyTranslator(props.user.group ?? "unknown")}
      </span>
      {/** Show the current status of the user, e.g. dead or alive */}
      <span>
        <strong>{keyTranslator("status")}: </strong>
        {keyTranslator(props.user.entry.status ?? "unknown")}
      </span>
      {/**
       * Show special buttons which are only available to the gamemaster. Allows
       * skipping to next phase and forcefully ending a game.
       */}
      {props.game.gamemaster === props.user.entry.id && (
        <div className="row-center-children">
          <SkipPhase gameId={props.game.id} />
          <ThrowGame />
        </div>
      )}
    </section>
  );
};

/**
 * Button which will skip the game to the next phase when clicked. Should only
 * be shown to the gamemaster or ideally not at all, but this is very useful for testing.
 */
const SkipPhase = (props: { gameId: number }) => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  return (
    <button
      onClick={() => {
        HTTP.Game.Phase.next(props.gameId)
          .then((res) => {
            if (HTTP.isError(res)) {
              toast.error(res.error);
            } else {
              toast.success(keyTranslator("successfullySubmitted"));
            }
            return null;
          })
          .catch(ToastErrorAny);
      }}
    >
      {keyTranslator("next") + " " + keyTranslator("phase", false)}
    </button>
  );
};

/**
 * Button which will end the game by killing all of the werewolves. Should only
 * be shown to the gamemaster.
 */
const ThrowGame = () => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  return (
    <button
      onClick={() => {
        HTTP.Lobby.throw().catch(ToastErrorAny);
      }}
    >
      {keyTranslator("throwGame")}
    </button>
  );
};

import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import expand from "../../../static/expand.svg";
import logoLight from "../../../static/logo-light.svg";
import HTTP from "../../../utils/api/http";
import {
  GameInfo,
  Player,
  Obituary,
  PlayerRole,
} from "../../../utils/api/types";
import { ToastErrorAny, TranslatorContext } from "../../Misc/Helpers";
import { LoadingAnimation } from "../../Misc/Loading";
import { Modal } from "../../Misc/Modal";

/**
 * Show overview of the game's current players. Shows an optionally scrollable
 * list of player names. Highlights players that are in the same group as the
 * user and shows an icon corresponding with that group next to the names. Shows
 * deceased players as crossed through and at the bottom of the list.
 */
export const Players = (props: {
  user: Player;
  userRoles: PlayerRole[];
  players: Player[];
  game: GameInfo;
}) => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  /** Obituary that is currently being viewed. Equals -1 if none is being viewed. */
  const [currentObituary, setCurrentObituary] = useState(-1);

  /** Content of the currently selected obituary. */
  const [obituary, setObituary] = useState<Obituary>();

  /** If the viewed obituary changes, fetch or delete obituary data */
  useEffect(() => {
    if (currentObituary >= 0) {
      HTTP.Game.Player.obituary(props.game.id, currentObituary)
        .then((res) => {
          if (HTTP.isError(res)) {
            toast.error(res.error);
          } else {
            setObituary(res);
          }
          return null;
        })
        .catch(ToastErrorAny);
    } else {
      setObituary(undefined);
    }
  }, [props.game.id, currentObituary]);

  /** Render a player into a string of text with optionally an icon */
  const renderPlayer = (player: Player, index: number, isDeceased: boolean) => {
    /** Add 'you' tag if the player is the current user */
    const isUser = props.user.entry.id === player.entry.id;
    const nameWithYou =
      player.entry.name +
      (isUser ? " (" + keyTranslator("you", false) + ")" : "");

    /** Make players in the same group have a bold name, except for townspeople. */
    const withBolding =
      isUser ||
      (player.group === props.user.group && player.group !== "townspeople") ? (
        <strong>{nameWithYou}</strong>
      ) : (
        <>{nameWithYou}</>
      );

    /** Strike through the names of deceased players */
    const withStriking = isDeceased ? (
      <del>{withBolding}</del>
    ) : (
      <>{withBolding}</>
    );

    /** Show relevant icon next to non-townspeople groups */
    const icon =
      player.group === "werewolves" ? (
        <img
          src={logoLight}
          className="tiny-size"
          alt={keyTranslator("werewolf")}
        />
      ) : (
        <></>
      );

    /** Put the elements together */
    const content = (
      <>
        {icon}
        {withStriking}
      </>
    );

    /** Make player clickable when deceased to view obituary */
    if (isDeceased) {
      return (
        <button
          key={index}
          className="tiny-margin"
          onClick={() => setCurrentObituary(player.entry.id)}
        >
          {content}
          <img
            src={expand}
            className="tiny-size"
            alt={keyTranslator("expand")}
          />
        </button>
      );
    } else {
      return <span key={index}>{content}</span>;
    }
  };

  return (
    <section className="column-center-children gap">
      <h2>{keyTranslator("playerList")}</h2>
      <div className="subsection scrollbox">
        <div className="column-center-children">
          {/**
           * Render the players which are alive first, and then render the players which
           * are not alive. This way it is more clear to the user which and how many
           * players are dead.
           */}
          {props.players
            .filter((player) => player.entry.status === "alive")
            .map((player, index) => renderPlayer(player, index, false))}
          {props.players
            .filter((player) => player.entry.status !== "alive")
            .map((player, index) => renderPlayer(player, index, true))}
        </div>
      </div>
      {currentObituary >= 0 && (
        <Modal close={() => setCurrentObituary(-1)}>
          <h3>
            {keyTranslator("obituary")} {keyTranslator("of", false)}{" "}
            {(() => {
              const player = props.players.find(
                (p) => p.entry.id === currentObituary
              );
              if (!player) return keyTranslator("unknown");
              if (props.user.entry.id === player.entry.id) {
                return (
                  player.entry.name + " (" + keyTranslator("you", false) + ")"
                );
              } else {
                return player.entry.name;
              }
            })()}
          </h3>
          {!obituary ? (
            /**
             * Show loading animation while the most recent obituary is still
             * being fetched.
             */
            <div className="padding-and-margin">
              <LoadingAnimation />
            </div>
          ) : (
            <>
              <div>
                <h4>{keyTranslator("roles")}</h4>
                <span>
                  {obituary.roles
                    .map((role, i) => keyTranslator(role.name, i === 0))
                    .join(", ")}
                </span>
              </div>

              {/**
               * Show deathnote or a message that the deceased player never submitted a
               * deathnote instead.
               */}
              <div>
                {obituary.deathNote ? (
                  <>
                    <h4>{keyTranslator("deathnote")}</h4>
                    <span>{obituary.deathNote}</span>
                  </>
                ) : (
                  <span className="greyed-out">
                    {keyTranslator("leftNoMessage")}
                  </span>
                )}
              </div>
            </>
          )}
          <button onClick={() => setCurrentObituary(-1)}>
            {keyTranslator("back")}
          </button>
        </Modal>
      )}
    </section>
  );
};

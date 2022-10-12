import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import HTTP from "../../../utils/api/http";
import {
  ActionList,
  ActionType,
  Bridges,
  GameInfo,
  Player,
} from "../../../utils/api/types";
import { ToastErrorAny, TranslatorContext } from "../../Misc/Helpers";
import { WithLabel, Select } from "../../Misc/Select";

/**
 * Actions screen which shows a box for each of the currently possible actions.
 * Allows selecting players, houses and bridges as targets.
 */
export const Actions = (props: {
  game: GameInfo;
  players: Player[];
  user: Player;
}) => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  const [actions, setActions] = useState<ActionList | null>(null);
  const [bridges, setBridges] = useState<Bridges | null>(null);

  /** Fetch available action list on first load and phase change */
  useEffect(() => {
    HTTP.Game.Purpose.actions(props.game.id)
      .then((res) => {
        if (HTTP.isError(res)) {
          toast.error(res.error);
        } else {
          setActions(res);
        }
        return null;
      })
      .catch(ToastErrorAny);
  }, [props.game.id, props.game.phase]);

  /** Fetch bridge names on first load */
  useEffect(() => {
    HTTP.Game.bridges(props.game.id)
      .then((res) => {
        if (HTTP.isError(res)) {
          toast.error(res.error);
        } else {
          setBridges(res);
        }
        return null;
      })
      .catch(ToastErrorAny);
  }, [props.game.id]);

  /** Don't render if there are no actions anyways */
  if (!actions || actions.actions.length === 0) return null;

  /** Don't render if the bridges haven't loaded */
  if (!bridges) return null;

  /** Filter out actions that don't have any options */
  const filteredActions = actions.actions.filter(
    (action) =>
      (action.eligible.PLAYER?.length ?? 0) > 0 ||
      (action.eligible.HOUSE?.length ?? 0) > 0 ||
      (action.eligible.BRIDGE?.length ?? 0) > 0
  );

  /** If after filtering no actions are left, don't render */
  if (filteredActions.length === 0) return null;

  return (
    <section className="column-center-children gap">
      <h2>{keyTranslator("actions")}</h2>
      {filteredActions.map((action, index) => (
        <Action
          key={index}
          game={props.game}
          type={action.action}
          playerOptions={
            /**
             * Transform array of player ID options to include the player name
             * such that the player name can be shown to the user while selecting.
             */
            action.eligible.PLAYER?.map((id) => ({
              value: id,
              label: (() => {
                const player = props.players.find((p) => p.entry.id === id);
                if (!player) return keyTranslator("unknown");
                if (props.user.entry.id === player?.entry.id) {
                  return (
                    player.entry.name + " (" + keyTranslator("you", false) + ")"
                  );
                } else {
                  return player.entry.name;
                }
              })(),
            })) ?? []
          }
          houseOptions={
            /**
             * Transform array of house ID options to include the name of the
             * player who owns the house such that the player name can be shown
             * to the user while selecting.
             */
            action.eligible.HOUSE?.map((id) => ({
              value: id,
              label: (() => {
                const player = props.players.find((p) => p.entry.id === id);
                if (!player) return keyTranslator("unknown");
                if (props.user.entry.id === player.entry.id) {
                  return (
                    player.entry.name + " (" + keyTranslator("you", false) + ")"
                  );
                } else {
                  return player.entry.name;
                }
              })(),
            })) ?? []
          }
          bridgeOptions={
            /**
             * Transform array of bridge ID options to include the name of the
             * bridge such that the user can see the name of the bridge while selecting.
             */
            action.eligible.BRIDGE?.map((id) => ({
              value: id,
              label:
                bridges.bridgeNames.find((b) => b.id === id)?.name ||
                id.toString(),
            })) ?? []
          }
          maxSelected={action.numberOfVotes}
        />
      ))}
    </section>
  );
};

/**
 * A subsection which represents a single running action option. Limits the
 * number of selected items depending on how many the action allows.
 */
const Action = (props: {
  type: ActionType;
  playerOptions: WithLabel<number>[];
  houseOptions: WithLabel<number>[];
  bridgeOptions: WithLabel<number>[];
  maxSelected: number;
  game: GameInfo;
}) => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  const [selectedPlayers, setSelectedPlayers] = useState<number[] | null>(null);
  const [selectedHouses, setSelectedHouses] = useState<number[] | null>(null);
  const [selectedBridges, setSelectedBridges] = useState<number[] | null>(null);

  /** Submit the action choice. */
  const handleSubmit = () => {
    HTTP.Game.Purpose.submit(
      props.game.id,
      props.type,
      selectedPlayers ?? [],
      selectedHouses ?? [],
      selectedBridges ?? []
    )
      .then((res) => {
        if (HTTP.isError(res)) {
          toast.error(res.error);
        } else {
          toast.success(keyTranslator("successfullySubmitted"));
        }
        return null;
      })
      .catch(ToastErrorAny);
  };

  /** Set limit on number of selected players, houses and bridges in total. */
  const maxSelectedReached =
    (selectedPlayers?.length ?? 0) +
      (selectedHouses?.length ?? 0) +
      (selectedBridges?.length ?? 0) >=
    props.maxSelected;

  return (
    <div className="subsection">
      <div className="column-center-children">
        <span>
          <strong>{keyTranslator(props.type)}</strong>
        </span>
        {props.playerOptions.length > 0 && (
          <div className="row-center-children">
            <span>{keyTranslator("player")}</span>
            {
              /**
               * If more than one option can be selected according to the action
               * and there are multiple options, render a multiselect element.
               * Else render a single select one, as only one can be selected anyways.
               */
              props.maxSelected > 1 && props.playerOptions.length > 1 ? (
                <Select
                  multiSelect={true}
                  options={props.playerOptions}
                  onChange={(s) =>
                    setSelectedPlayers(s.map((o) => o.value.value))
                  }
                  display={(p) => p.label}
                  allOptionsDisabled={maxSelectedReached}
                />
              ) : (
                <Select
                  multiSelect={false}
                  options={props.playerOptions}
                  onChange={(s) => setSelectedPlayers(s ? [s.value.value] : [])}
                  display={(p) => p.label}
                  allOptionsDisabled={maxSelectedReached}
                />
              )
            }
          </div>
        )}
        {props.houseOptions.length > 0 && (
          <div className="row-center-children">
            <span>{keyTranslator("house")}</span>
            {
              /**
               * If more than one option can be selected according to the action
               * and there are multiple options, render a multiselect element.
               * Else render a single select one, as only one can be selected anyways.
               */
              props.maxSelected > 1 && props.houseOptions.length > 1 ? (
                <Select
                  multiSelect={true}
                  options={props.houseOptions}
                  onChange={(s) =>
                    setSelectedHouses(s.map((o) => o.value.value))
                  }
                  display={(p) => p.label}
                  allOptionsDisabled={maxSelectedReached}
                />
              ) : (
                <Select
                  multiSelect={false}
                  options={props.houseOptions}
                  onChange={(s) => setSelectedHouses(s ? [s.value.value] : [])}
                  display={(p) => p.label}
                  allOptionsDisabled={maxSelectedReached}
                />
              )
            }
          </div>
        )}
        {props.bridgeOptions.length > 0 && (
          <div className="row-center-children">
            <span>{keyTranslator("bridge")}</span>
            {
              /**
               * If more than one option can be selected according to the action
               * and there are multiple options, render a multiselect element.
               * Else render a single select one, as only one can be selected anyways.
               */
              props.maxSelected > 1 && props.bridgeOptions.length > 1 ? (
                <Select
                  multiSelect={true}
                  options={props.bridgeOptions}
                  onChange={(s) =>
                    setSelectedBridges(s.map((o) => o.value.value))
                  }
                  display={(p) => p.label}
                  allOptionsDisabled={maxSelectedReached}
                />
              ) : (
                <Select
                  multiSelect={false}
                  options={props.bridgeOptions}
                  onChange={(s) => setSelectedBridges(s ? [s.value.value] : [])}
                  display={(p) => p.label}
                  allOptionsDisabled={maxSelectedReached}
                />
              )
            }
          </div>
        )}
        <button onClick={handleSubmit}>{keyTranslator("send")}</button>
      </div>
    </div>
  );
};

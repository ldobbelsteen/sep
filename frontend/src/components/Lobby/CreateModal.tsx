import { Chance } from "chance";
import React, { useCallback, useState } from "react";
import { toast } from "react-hot-toast";
import HTTP from "../../utils/api/http";
import Schema from "../../utils/api/schema";
import { LobbyConfig } from "../../utils/api/types";
import { capitalizeFirstLetter } from "../../utils/tools";
import { ToastErrorAny, TranslatorContext } from "../Misc/Helpers";
import { LoadingAnimation } from "../Misc/Loading";
import { Modal } from "../Misc/Modal";

/** Get random integer in a specified range */
const randomInteger = (min: number, max: number) => {
  min = Math.ceil(min);
  max = Math.floor(max);
  return Math.floor(Math.random() * (max - min + 1)) + min;
};

/**
 * Generate default lobby configuration using randomized name and a randomized 6
 * digit seed.
 */
const defaultConfig = (): LobbyConfig => {
  const random = new Chance().word({ syllables: 2 });
  return {
    gameName: capitalizeFirstLetter(random),
    seed: randomInteger(100000, 999999),
  };
};

/**
 * Interface where a new lobby can be created. Allows the user to modify several
 * game configuration settings and submit them to the API to create a lobby.
 */
export const CreateModal = (props: {
  close: () => void;
  success: (gameId: number) => void;
}) => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  const [config, setConfig] = useState(defaultConfig());
  const [submitting, setSubmitting] = useState(false);

  /**
   * Update the config by merging the current config and a new (partial) config
   * by overriding the old values with the new.
   */
  const updateConfig = useCallback(
    (update: Partial<LobbyConfig>) => {
      setConfig({ ...config, ...update });
    },
    [config]
  );

  /**
   * Submit the current config and submit the game ID of the newly created game
   * back to the parent such that a lobby can be rendered.
   */
  const handleCreate = () => {
    if (Schema.isValid(config, Schema.LobbyConfig)) {
      setSubmitting(true);
      HTTP.Lobby.create(config)
        .then((res) => {
          if (HTTP.isError(res)) {
            toast.error(res.error);
          } else {
            props.success(res.gameId);
          }
          return null;
        })
        .finally(() => setSubmitting(false))
        .catch(ToastErrorAny);
    } else {
      toast.error(keyTranslator("invalidConfig"));
    }
  };

  /**
   * Render all of the configuration options together with the submission and
   * back buttons.
   */
  return (
    <Modal close={props.close}>
      {submitting ? (
        /** Show loading animation while submitting */
        <LoadingAnimation />
      ) : (
        <>
          <h2>{keyTranslator("createGame")}</h2>
          {/** Configure the game's canonical name */}
          <div className="row-center-children">
            {keyTranslator("gameName")}
            <input
              type="text"
              value={config.gameName}
              onChange={(ev) => updateConfig({ gameName: ev.target.value })}
              placeholder={keyTranslator("gameName")}
            />
          </div>
          {/**
           * Configure the game's seed. This allows the user to create a game with
           * deterministically distributed role assignments.
           */}
          <div className="row-center-children">
            {keyTranslator("seed")}
            <input
              type="number"
              min={0}
              max={999999}
              value={config.seed}
              onChange={(ev) =>
                updateConfig({ seed: parseInt(ev.target.value) })
              }
              placeholder={keyTranslator("seed")}
            />
          </div>
          <div className="row-center-children">
            <button onClick={props.close}>{keyTranslator("back")}</button>
            <button onClick={handleCreate}>{keyTranslator("create")}</button>
          </div>
        </>
      )}
    </Modal>
  );
};

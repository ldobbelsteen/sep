import React, { useState } from "react";
import create from "../../static/create.svg";
import join from "../../static/join.svg";
import { TranslatorContext } from "../Misc/Helpers";
import { Logout } from "../Misc/Logout";
import { Profile } from "../Profile/Profile";
import { CreateModal } from "./CreateModal";
import { JoinModal } from "./JoinModal";

/**
 * Pre-lobby component which allows the user to choose between joining a lobby
 * or creating one themselves.
 */
export const Landing = (props: {
  userId: number;
  userName: string;
  setGameId: (id: number) => void;
}) => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  const [creatingLobby, setCreatingLobby] = useState(false);
  const [joiningLobby, setJoiningLobby] = useState(false);

  /**
   * Show buttons which will let the user choose between joining and creating a
   * lobby. Allows the user to logout or change the language.
   */
  return (
    <div className="full-size column-center-children">
      <h3>
        {keyTranslator("welcome")}, {props.userName}!
      </h3>
      <div className="row-center-children">
        <button
          className="large-square-button column-center-children"
          onClick={() => setCreatingLobby(true)}
        >
          <img src={create} alt={keyTranslator("createGame")} />
          <span>{keyTranslator("createGame")}</span>
        </button>
        <button
          className="large-square-button column-center-children"
          onClick={() => setJoiningLobby(true)}
        >
          <img src={join} alt={keyTranslator("joinGame")} />
          <span>{keyTranslator("joinGame")}</span>
        </button>
      </div>
      <div className="row-center-children">
        <Logout />
        <Profile loggedIn={true} currentUsername={props.userName} />
      </div>
      {
        /**
         * Show lobby creation interface if the user has clicked the create
         * lobby button. Supplies a back button which will bring the user 'back'
         * to this component.
         */
        creatingLobby && (
          <CreateModal
            close={() => setCreatingLobby(false)}
            success={(id) => props.setGameId(id)}
          />
        )
      }
      {
        /**
         * Show lobby joining interface if the user has clicked the join lobby
         * button. Supplies a back button which will bring the user 'back' to
         * this component.
         */
        joiningLobby && (
          <JoinModal
            close={() => setJoiningLobby(false)}
            success={(id) => props.setGameId(id)}
          />
        )
      }
    </div>
  );
};

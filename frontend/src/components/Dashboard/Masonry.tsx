import React from "react";
import { GameInfo, Player, PlayerRole } from "../../utils/api/types";
import WS from "../../utils/api/ws";
import { Logout } from "../Misc/Logout";
import { Profile } from "../Profile/Profile";
import { Actions } from "./Sections/Actions";
import { Chats } from "./Sections/Chats";
import { Deathnote } from "./Sections/Deathnote";
import { Info } from "./Sections/Info";
import { Messages } from "./Sections/Messages";
import { Players } from "./Sections/Players";
import { Roles } from "./Sections/Roles";
import { Votes } from "./Sections/Votes";

/**
 * Renders the different game interaction sections based on the data fetched by
 * the dashboard component. Shows sections in a masonry style in order to be
 * responsive to different screen sizes.
 */
export const Masonry = (props: {
  game: GameInfo;
  user: Player;
  userRoles: PlayerRole[];
  players: Player[];
  socket: WS;
}) => {
  /**
   * Render the sections by taking up the full screen and centering them
   * columnwise. The masonry causes the sections to be rendered in a maximum of
   * three columns. If the sections are too wide, less than 3 columns could be
   * rendered. The maximum prevents the sections from spreading out too much on
   * large screens.
   */
  return (
    <div className="full-size column-center-children">
      <div className="masonry">
        <Info user={props.user} userRoles={props.userRoles} game={props.game} />
        <Chats
          user={props.user}
          game={props.game}
          players={props.players}
          socket={props.socket}
        />
        {props.user.entry.status !== "deceased" && (
          <Votes
            user={props.user}
            userRoles={props.userRoles}
            game={props.game}
            players={props.players}
          />
        )}
        <Players
          user={props.user}
          userRoles={props.userRoles}
          players={props.players}
          game={props.game}
        />
        <Roles />
        {props.user.entry.status !== "deceased" && (
          <Deathnote user={props.user} game={props.game} />
        )}
        <Actions game={props.game} players={props.players} user={props.user} />
        <Messages game={props.game} />
      </div>
      <div className="row-center-children">
        <Logout />
        <Profile loggedIn={true} currentUsername={props.user.entry.name} />
      </div>
    </div>
  );
};

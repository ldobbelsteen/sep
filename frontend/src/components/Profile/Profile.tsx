import React, { useState } from "react";
import { TranslatorContext } from "../Misc/Helpers";
import { LanguageSelect } from "../Misc/LanguageSelect";
import { Modal } from "../Misc/Modal";
import { GPDR } from "./GDPR";
import { UpdateName } from "./UpdateName";

/**
 * Modal which allows the user to update their information like their user name
 * and export/delete their data to adhere to the GDPR.
 */
export const Profile = (props: {
  loggedIn: boolean;
  currentUsername?: string;
}) => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  const [opened, setOpened] = useState(false);

  /**
   * Render opening button and optionally profile modal if it is currently
   * opened. Only shows GDPR and name update components when the user is logged in.
   */
  return (
    <>
      <button onClick={() => setOpened(true)}>
        {keyTranslator("profile")}
      </button>
      {opened && (
        <Modal close={() => setOpened(false)}>
          <h3>{keyTranslator("profile")}</h3>
          {props.loggedIn && <GPDR />}
          {props.loggedIn && props.currentUsername && (
            <UpdateName currentUsername={props.currentUsername} />
          )}
          <LanguageSelect />
          <button onClick={() => setOpened(false)}>
            {keyTranslator("back")}
          </button>
        </Modal>
      )}
    </>
  );
};

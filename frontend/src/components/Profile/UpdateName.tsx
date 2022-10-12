import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import HTTP from "../../utils/api/http";
import { ToastErrorAny, TranslatorContext } from "../Misc/Helpers";

/**
 * Allow updating the currently logged in user's username with this form wrapped
 * in a section. Requires the current username to have as a default value.
 */
export const UpdateName = (props: { currentUsername: string }) => {
  const { keyTranslator } = React.useContext(TranslatorContext);

  const [newUsername, setNewUsername] = useState("");

  /** Populate input text with current username when it changes */
  useEffect(() => {
    setNewUsername(props.currentUsername);
  }, [props.currentUsername]);

  /**
   * Render the name update input and button as a form, such that pressing enter
   * will submit the change.
   */
  return (
    <section>
      <form
        onSubmit={(ev) => {
          ev.preventDefault();
          HTTP.User.GDPR.updateUserInfo({
            dataKey: "USERNAME",
            data: newUsername,
          })
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
        <h4>{keyTranslator("username")}</h4>
        <input
          type="text"
          value={newUsername}
          onChange={(ev) => setNewUsername(ev.target.value)}
          placeholder={keyTranslator("username")}
        />
        <button type="submit">{keyTranslator("send")}</button>
      </form>
    </section>
  );
};
